package ru.netology.repository

import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.Post

interface PostDataRepository {
    val data: Flow<List<Post>>
    fun getNewerCount(id: Long): Flow<Int>
    suspend fun getAll(): List<Post>
    suspend fun likeById(id: Long): Post
    suspend fun dislikeById(id: Long): Post
    suspend fun removeById(id: Long)
    suspend fun save(post: Post)
}