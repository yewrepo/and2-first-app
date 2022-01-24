package ru.netology.vm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.Post
import ru.netology.repository.PostRepository
import ru.netology.repository.PrefsPostRepositoryImpl

private var emptyPost = Post(
    id = 0,
    author = "",
    content = "",
    published = "",
    likes = 0,
    share = 0,
    view = 0
)

class PostViewModel(
    app: Application,
) : AndroidViewModel(app) {

    private val repository: PostRepository = PrefsPostRepositoryImpl(app)
    val data = repository.get()

    private val edited = MutableLiveData(emptyPost)
    val editPost: LiveData<Post>
        get() = edited

    fun edit(post: Post) {
        edited.value = post
    }

    fun save() {
        edited.value?.let {
            repository.save(it)
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

    fun likeById(id: Int) = repository.likeById(id)
    fun shareById(id: Int) = repository.shareById(id)
    fun removeById(id: Int) = repository.removeById(id)
}
