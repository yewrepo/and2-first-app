package ru.netology.vm

import android.app.Application
import android.net.Uri
import androidx.lifecycle.*
import ru.netology.nmedia.Post
import ru.netology.repository.*
import kotlinx.coroutines.launch
import ru.netology.AppDb
import ru.netology.datasource.RetrofitPostSourceImpl
import ru.netology.datasource.RoomPostSourceImpl
import ru.netology.network.ApiClient
import ru.netology.network.AppError
import ru.netology.nmedia.NmediaApp
import ru.netology.nmedia.PhotoModel
import ru.netology.nmedia.R
import java.io.File
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

    val data = repository.data.map(::FeedModel)

    private val _loadingState = MutableLiveData(LoadingState())
    val loadingState: LiveData<LoadingState>
        get() = _loadingState

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
                execute(defaultMessage, _loadingState) {
                    repository.save(post = it)
                    _postCreated.postValue(Unit)
                    edited.value = emptyPost
                }
            }
        }
    }

    fun cancel() {
        edited.value = emptyPost
    }

    fun removePhoto() {
        edited.value?.let { post ->
            edited.postValue(post.copy(photoModel = null))
        }
    }

    fun changePhoto(uri: Uri?, file: File?) {
        edited.value?.let { post ->
            edited.postValue(post.copy(photoModel = PhotoModel(uri, file)))
        }
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
            execute(defaultMessage, _loadingState) {
                if (liked) {
                    repository.likeById(id)
                } else {
                    repository.dislikeById(id)
                }
            }
        }
    }

    fun removeById(id: Long) {
        viewModelScope.launch {
            execute(defaultMessage, _loadingState) {
                repository.removeById(id)
            }
        }
    }

    fun loadPosts() {
        viewModelScope.launch {
            execute(defaultMessage, _loadingState) {
                repository.getAll()
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
    data: MutableLiveData<LoadingState>,
    block: suspend () -> Unit
) {
    try {
        data.postValue(LoadingState(isLoading = true))
        block()
        data.postValue(LoadingState())
    } catch (e: Exception) {
        data.postValue(
            LoadingState(
                isError = true,
                errorDescription = e.toErrorModel(defaultMessage)
            )
        )
    }
}
