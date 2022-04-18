package ru.netology.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.netology.model.Post

interface PostDataRepository {
    val data: Flow<PagingData<Post>>
    fun getNewerCount(id: Long): Flow<Int>
    suspend fun getAll(): List<Post>
    suspend fun likeById(id: Long): Post
    suspend fun dislikeById(id: Long): Post
    suspend fun removeById(id: Long)
    suspend fun save(post: Post)
}