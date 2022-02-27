package ru.netology.vm

import android.app.Application
import android.os.Handler
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.Post
import ru.netology.repository.*
import android.os.Looper
import retrofit2.HttpException
import ru.netology.nmedia.NmediaApp
import ru.netology.nmedia.R

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

    var mainHandler = Handler(Looper.getMainLooper())

    private val repository: PostAsyncRepository = RetrofitPostRepositoryImpl()
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
        edited.value?.let {
            repository.save(it, callback = object : CompleteCallback {
                override fun onSuccess() {
                    mainHandler.post {
                        _postCreated.postValue(Unit)
                        edited.value = emptyPost
                    }
                }

                override fun onError(e: Throwable) {
                    _data.postValue(
                        FeedModel(
                            error = true,
                            errorDescription = e.toErrorModel(defaultMessage)
                        )
                    )
                }
            })
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

    fun likeById(id: Int, liked: Boolean) {
        if (liked) {
            repository.dislikeById(id, completeCallback(id))
        } else {
            repository.likeById(id, completeCallback(id))
        }
    }

    private fun completeCallback(id: Int) = object : CompleteCallback {
        override fun onSuccess() {
            _data.postValue(
                _data.value?.copy(posts = _data.value?.posts.orEmpty().let { list ->
                    val newList = list.toMutableList()
                    for ((index, post) in list.withIndex()) {
                        if (post.id == id) {
                            newList[index] = post.copy(
                                likedByMe = !post.likedByMe,
                                likes = post.likes + (if (post.likedByMe) -1 else 1)
                            )
                        }
                    }
                    newList
                })
            )
        }

        override fun onError(e: Throwable) {
            val old = _data.value?.posts.orEmpty()
            _data.postValue(_data.value?.copy(posts = old))
        }
    }

    fun shareById(id: Int) {
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

    fun removeById(id: Int) {
        repository.removeById(id, object : CompleteCallback {
            override fun onSuccess() {
                val old = _data.value?.posts.orEmpty()
                _data.postValue(
                    _data.value?.copy(posts = _data.value?.posts.orEmpty().filter { it.id != id }
                    )
                )
                _data.postValue(_data.value?.copy(posts = old))
            }

            override fun onError(e: Throwable) {
                _data.postValue(
                    FeedModel(
                        error = true,
                        errorDescription = e.toErrorModel(defaultMessage)
                    )
                )
            }
        })
    }

    fun loadPosts() {
        _data.postValue(FeedModel(loading = true))
        repository.get(object : PostListCallback {
            override fun onSuccess(list: List<Post>) {
                _data.postValue(FeedModel(posts = list, loading = false))
            }

            override fun onError(e: Throwable) {
                _data.postValue(
                    FeedModel(
                        error = true,
                        errorDescription = e.toErrorModel(defaultMessage)
                    )
                )
            }
        })
    }
}

private fun Throwable.toErrorModel(defaultMessage: String): ErrorData {
    return if (this is HttpException) {
        ErrorData(this.message())
    } else {
        ErrorData(defaultMessage)
    }
}
