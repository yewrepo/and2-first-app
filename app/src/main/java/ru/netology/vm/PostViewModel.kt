package ru.netology.vm

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import ru.netology.nmedia.Post
import ru.netology.repository.*
import ru.netology.AppDb
import ru.netology.datasource.RetrofitPostSourceImpl
import ru.netology.datasource.RoomPostSourceImpl
import ru.netology.network.ApiClient
import ru.netology.network.AppError
import ru.netology.nmedia.NmediaApp
import ru.netology.nmedia.R
import kotlin.Exception

private var emptyPost = Post(
    id = 0,
    author = "Me",
    content = "",
    published = 0,
    likes = 0,
    share = 0,
    view = 0,
    isNew = false
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

    val data = liveData(
        viewModelScope.coroutineContext + Dispatchers.Default
    ) {
        repository.data
            .map(::FeedModel).collect {
                emit(it)
            }
    }

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

    fun requestUpdates() {
        viewModelScope.launch(Dispatchers.Main) {
            CoroutineScope(Dispatchers.IO).launchPeriodicAsync(10_000) {
                launch {
                    try {
                        val firstId = data.value?.posts?.firstOrNull()?.id ?: 0L
                        val count = repository.getNewerCount(firstId).single()
                        if (count > 0) {
                            _loadingState.value?.apply {
                                _loadingState.postValue(copy(newPostNotify = true))
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("PostViewModel", "error: $e")
                    }
                }
            }.join()
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

fun CoroutineScope.launchPeriodicAsync(
    repeatMillis: Long,
    action: () -> Unit
) = this.launch {
    if (repeatMillis > 0) {
        while (isActive) {
            action()
            delay(repeatMillis)
        }
    } else {
        action()
    }
}