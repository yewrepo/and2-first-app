package ru.netology.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.netology.extension.likeOrNot
import ru.netology.extension.shareMe
import ru.netology.nmedia.Post

class PrefsPostRepositoryImpl(c: Context) : PostRepository {

    private val gson = Gson()
    private val prefs = c.getSharedPreferences("repo", Context.MODE_PRIVATE)
    private val type = TypeToken.getParameterized(List::class.java, Post::class.java).type
    private val key = "posts"
    private var nextId: Int = 1
    private var posts = listOf<Post>()

    private val data: MutableLiveData<List<Post>> = MutableLiveData()

    init {
        prefs.getString(key, null)?.let {
            posts = gson.fromJson(it, type)
            data.value = posts
            nextId = posts.size
        }
    }

    override fun get(): LiveData<List<Post>> = data

    override fun likeById(id: Int) {
        posts.map { p ->
            if (p.id == id) {
                p.likeOrNot()
            } else {
                p
            }
        }.apply {
            posts = this
            data.value = this
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
            posts = this
            data.value = this
            sync()
        }
    }

    override fun removeById(id: Int) {
        posts.filter { p ->
            p.id != id
        }.apply {
            posts = this
            data.value = this
            sync()
        }
    }

    override fun save(post: Post) {
        if (post.id == 0) {
            posts =
                listOf(
                    post.copy(
                        id = nextId++,
                        author = "Me",
                        published = "now"
                    )
                ) + posts
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

    override fun findById(id: Int): Post? = posts.find {
        it.id == id
    }

    private fun sync() {
        with(prefs.edit()) {
            putString(key, gson.toJson(posts))
            apply()
        }
    }
}