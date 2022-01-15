package ru.netology.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.netology.extension.likeOrNot
import ru.netology.extension.shareMe
import ru.netology.nmedia.Post

class PostRepositoryFileImpl(c: Context) : PostRepository {

    private val gson = Gson()
    private val prefs = c.getSharedPreferences("repo", Context.MODE_PRIVATE)
    private val type = TypeToken.getParameterized(List::class.java, Post::class.java).type
    private val key = "posts"
    private var nextId: Int = 1
    private var posts = mutableListOf<Post>()

    private val data: MutableLiveData<MutableList<Post>> = MutableLiveData()

    init {
        prefs.getString(key, null)?.let {
            posts = gson.fromJson(it, type)
            data.value = posts
        }
    }

    override fun get(): LiveData<MutableList<Post>> = data

    override fun likeById(id: Int) {
        posts.map { p ->
            if (p.id == id) {
                p.likeOrNot()
            } else {
                p
            }
        }.apply {
            posts = this.toMutableList()
            data.value = this.toMutableList()
            sync()
        }
    }

    override fun shareById(id: Int) {
        posts.map { p ->
            if (p.id == id) {
                p.shareMe()
            } else {
                p
            }
        }.apply {
            posts = this.toMutableList()
            data.value = this.toMutableList()
            sync()
        }
    }

    override fun removeById(id: Int) {
        posts.filter { p ->
            p.id != id
        }.apply {
            posts = this.toMutableList()
            data.value = this.toMutableList()
            sync()
        }
    }

    override fun save(post: Post) {
        if (post.id == 0) {
            posts =
                (mutableListOf(
                    post.copy(
                        id = nextId++,
                        author = "Me",
                        published = "now"
                    )
                ) + posts) as MutableList<Post>
        } else {
            posts = posts.map { p ->
                if (p.id == post.id) {
                    p.copy(content = post.content)
                } else {
                    p
                }
            }.toMutableList()
        }
        data.value = posts
        sync()
    }

    private fun sync() {
        with(prefs.edit()) {
            putString(key, gson.toJson(posts))
            apply()
        }
    }
}