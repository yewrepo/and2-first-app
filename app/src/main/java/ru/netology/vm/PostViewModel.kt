package ru.netology.vm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.Post
import ru.netology.repository.*
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.netology.AppDb
import ru.netology.datasource.RetrofitPostSourceImpl
import ru.netology.datasource.RoomPostSourceImpl
import ru.netology.network.ApiClient
import ru.netology.network.AppError
import ru.netology.nmedia.NmediaApp
import ru.netology.nmedia.R
import java.lang.Exception

private var emptyPost = Post(
    id = 0,
    author = "Me",
    content = "",
    published = 0,
    likes = 0,
    share = 0,
    view = 0
)

class PostViewModel(
    app: Application,
) : AndroidViewModel(app) {

    private val defaultMessage =
        getApplication<NmediaApp>().getString(R.string.error_request_message)

    private val repository: PostDataRepository = RetrofitPostRepositoryImpl(
        RetrofitPostSourceImpl(ApiClient.retrofitService),
        RoomPostSourceImpl(AppDb.getInstance(app.applicationContext).postDao())
    )

    private val _data = MutableLiveData(FeedModel())
    val data: LiveData<FeedModel>
        get() = _data

    private val edited = MutableLiveData(emptyPost)
    val editPost: LiveData<Post>
        get() = edited

    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    fun edit(post: Post) {
        edited.value = post
    }

    fun save() {
        viewModelScope.launch {
            edited.value?.let {
                execute(defaultMessage, _data) {
                    repository.save(post = it)
                    _postCreated.postValue(Unit)
                    edited.value = emptyPost
                    _data.postValue(FeedModel(posts = repository.getAll(true)))
                }
            }
        }
    }

    fun cancel() {
        edited.value = emptyPost
    }

    fun changeContent(content: String, youtubeLink: String?) {
        edited.value?.let { post ->
            val text = content.trim()
            if (post.content != text || post.youtubeLink != youtubeLink) {
                edited.value = post.copy(
                    content = text,
                    youtubeLink = youtubeLink
                )
            }
        }
    }

    fun likeById(id: Long, liked: Boolean) {
        viewModelScope.launch {
            execute(defaultMessage, _data) {
                if (liked) {
                    repository.likeById(id)
                } else {
                    repository.dislikeById(id)
                }
                _data.postValue(FeedModel(posts = repository.getAll(false)))
            }
        }
    }

    fun shareById(id: Long) {
        _data.postValue(
            _data.value?.copy(posts = _data.value?.posts.orEmpty().let { list ->
                val newList = list.toMutableList()
                for ((index, post) in list.withIndex()) {
                    if (post.id == id) {
                        newList[index] = post.copy(
                            share = post.share + 1
                        )
                    }
                }
                newList
            })
        )
    }

    fun removeById(id: Long) {
        viewModelScope.launch {
            execute(defaultMessage, _data) {
                repository.removeById(id)
                _data.postValue(FeedModel(posts = repository.getAll(false)))
            }
        }
    }

    fun loadPosts() {
        viewModelScope.launch {
            execute(defaultMessage, _data) {
                _data.postValue(FeedModel(posts = repository.getAll(true)))
            }
        }
    }
}

private fun Throwable.toErrorModel(defaultMessage: String): ErrorData {
    return if (this is AppError) {
        ErrorData(this.code)
    } else {
        ErrorData(defaultMessage)
    }
}

private suspend fun execute(
    defaultMessage: String,
    data: MutableLiveData<FeedModel>,
    block: suspend () -> Unit
) {
    try {
        data.postValue(FeedModel(loading = true, posts = data.value?.posts ?: emptyList()))
        block()
    } catch (e: Exception) {
        data.postValue(
            FeedModel(
                error = true,
                errorDescription = e.toErrorModel(defaultMessage)
            )
        )
    }
}
