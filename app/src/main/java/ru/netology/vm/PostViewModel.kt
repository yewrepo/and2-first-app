package ru.netology.vm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.Post
import ru.netology.repository.NetworkPostRepositoryImpl
import ru.netology.repository.PostRepository
import java.lang.Exception
import kotlin.concurrent.thread

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

    private val repository: PostRepository = NetworkPostRepositoryImpl()
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
            thread {
                repository.save(it)
                _postCreated.postValue(Unit)
            }
        }
        edited.value = emptyPost
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
        thread {
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
    }

    fun loadPosts() {
        thread {
            _data.postValue(FeedModel(loading = true))
            try {
                val posts = repository.get()
                _data.postValue(FeedModel(posts = posts.value!!, loading = false))
            } catch (e: Exception) {
                _data.postValue(FeedModel(error = true))
            }
        }
    }
}
