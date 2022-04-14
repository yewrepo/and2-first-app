package ru.netology.vm

import android.app.Application
import android.net.Uri
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import ru.netology.AppAuth
import ru.netology.nmedia.Post
import ru.netology.repository.*
import ru.netology.extension.getEmptyPost
import ru.netology.network.AppError
import ru.netology.nmedia.NmediaApp
import ru.netology.nmedia.PhotoModel
import ru.netology.nmedia.R
import java.io.File
import javax.inject.Inject
import kotlin.Exception

@HiltViewModel
class PostViewModel @Inject constructor(
    app: Application,
    private val repository: PostDataRepository,
    private val appAuth: AppAuth
) : AndroidViewModel(app) {

    private val defaultMessage =
        getApplication<NmediaApp>().getString(R.string.error_request_message)

    private val cached = repository
        .data
        .cachedIn(viewModelScope)

    val data: Flow<PagingData<Post>> = appAuth.authStateFlow
        .flatMapLatest { (myId, _) ->
            cached.map { pagingData ->
                pagingData.map { post ->
                    post.copy(ownedByMe = post.authorId == myId)
                }
            }
        }

    private val _loadingState = MutableLiveData(LoadingState())
    val loadingState: LiveData<LoadingState>
        get() = _loadingState

    private val edited = MutableLiveData(getEmptyPost(getId()))
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
                    edited.value = getEmptyPost(getId())
                }
            }
        }
    }

    fun cancel() {
        edited.value = getEmptyPost(getId())
    }

    private fun getId() = appAuth.authStateFlow.value.id

    fun removePhoto() {
        edited.value?.let { post ->
            edited.postValue(post.copy(attachment = null))
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
           /* execute(defaultMessage, _loadingState) {
                repository.getAll()
            }*/
        }
    }

    fun requestUpdates() {
        /* viewModelScope.launch(Dispatchers.Main) {
             CoroutineScope(Dispatchers.IO).launchPeriodicAsync(20_000) {
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
         }*/
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