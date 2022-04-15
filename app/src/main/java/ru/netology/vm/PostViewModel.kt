package ru.netology.vm

import android.app.Application
import android.net.Uri
import android.util.Log
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
import ru.netology.nmedia.PhotoModel
import java.io.File
import javax.inject.Inject
import kotlin.Exception

@HiltViewModel
class PostViewModel @Inject constructor(
    app: Application,
    private val repository: PostDataRepository,
    private val appAuth: AppAuth
) : AndroidViewModel(app) {

    private val cached = repository
        .data
        .cachedIn(viewModelScope)

    val data: Flow<PagingData<Post>> = appAuth.authStateFlow
        .flatMapLatest { (_, _) ->
            cached.map { pagingData ->
                pagingData
            }
        }

    private val _newPostsNotify = MutableLiveData(LoadingState())
    val newPostsNotify: LiveData<LoadingState>
        get() = _newPostsNotify

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
                repository.save(post = it)
                _postCreated.postValue(Unit)
                edited.value = getEmptyPost(getId())
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
            if (liked) {
                repository.likeById(id)
            } else {
                repository.dislikeById(id)
            }
        }
    }

    fun removeById(id: Long) {
        viewModelScope.launch {
            repository.removeById(id)
        }
    }

    fun requestUpdates() {
        viewModelScope.launch(Dispatchers.Main) {
            CoroutineScope(Dispatchers.IO).launchPeriodicAsync(20_000) {
                launch {
                    try {
                        val firstId = repository.getAll().firstOrNull()?.id ?: 0L
                        val count = repository.getNewerCount(firstId).single()
                        _newPostsNotify.value?.apply {
                            _newPostsNotify.postValue(copy(newPostNotify = count > 0))
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