package ru.netology.vm

import android.app.Application
import android.os.Handler
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.Post
import ru.netology.repository.*
import java.lang.Exception
import kotlin.concurrent.thread
import android.os.Looper

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

    private val tag = this.javaClass.simpleName

    var mainHandler = Handler(Looper.getMainLooper())

    private val repository: PostAsyncRepository = NetworkAsyncPostRepositoryImpl()
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
                    Log.e(tag, "" + e)
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
        thread {
            val old = _data.value?.posts.orEmpty()
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

            try {
                if (liked) {
                    repository.dislikeById(id)
                } else {
                    repository.likeById(id)
                }
            } catch (e: Exception) {
                _data.postValue(_data.value?.copy(posts = old))
            }
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

        /*thread {
            repository.shareById(id)
        }*/
    }

    fun removeById(id: Int) {
        val old = _data.value?.posts.orEmpty()
        _data.postValue(
            _data.value?.copy(posts = _data.value?.posts.orEmpty().filter { it.id != id }
            )
        )
        try {
            repository.removeById(id)
        } catch (e: Exception) {
            _data.postValue(_data.value?.copy(posts = old))
        }
    }

    fun loadPosts() {
        _data.postValue(FeedModel(loading = true))
        val posts = repository.get(object : PostListCallback {
            override fun onSuccess(list: List<Post>) {
                _data.postValue(FeedModel(posts = list, loading = false))
            }

            override fun onError(e: Throwable) {
                _data.postValue(FeedModel(error = true))
            }
        })
    }
}
