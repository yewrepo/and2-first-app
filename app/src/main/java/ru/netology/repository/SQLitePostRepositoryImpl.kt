package ru.netology.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.db.dao.PostDao
import ru.netology.extension.likeOrNot
import ru.netology.extension.shareMe
import ru.netology.nmedia.Post

class SQLitePostRepositoryImpl(
    private val dao: PostDao
) : PostRepository {

    private var posts = emptyList<Post>()
    private val data = MutableLiveData(posts)

    init {
        posts = dao.getAll()
        data.value = posts
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
            dao.likeById(id)
            posts = this
            data.value = this
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
            dao.shareById(id)
            posts = this
            data.value = this
        }
    }

    override fun removeById(id: Int) {
        posts.filter { p ->
            p.id != id
        }.apply {
            dao.removeById(id)
            posts = this
            data.value = this
        }
    }

    override fun save(post: Post) {
        val id = post.id
        val saved = dao.save(post)
        posts = if (id == 0) {
            listOf(saved) + posts
        } else {
            posts.map {
                if (it.id != id) it else saved
            }
        }
        data.value = posts
    }
}