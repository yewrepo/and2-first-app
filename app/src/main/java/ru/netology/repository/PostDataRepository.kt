package ru.netology.repository

import androidx.lifecycle.LiveData
import ru.netology.nmedia.Post

interface PostDataRepository {
    val data: LiveData<List<Post>>
    suspend fun getAll(): List<Post>
    suspend fun likeById(id: Long): Post
    suspend fun dislikeById(id: Long): Post
    suspend fun removeById(id: Long)
    suspend fun save(post: Post)
}