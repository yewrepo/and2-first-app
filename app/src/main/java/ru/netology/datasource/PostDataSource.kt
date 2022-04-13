package ru.netology.datasource

import androidx.paging.PagingSource
import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.Media
import ru.netology.nmedia.MediaUpload
import ru.netology.nmedia.Post

interface PostDataSource {
    fun get(): Flow<List<Post>>
    fun pagingSource(): PagingSource<Int, Post>
    suspend fun getNewer(id: Long):  List<Post>
    suspend fun getAll(): List<Post>
    suspend fun likeById(id: Long): Post
    suspend fun dislikeById(id: Long): Post
    suspend fun removeById(id: Long)
    suspend fun save(post: Post): Post
    suspend fun save(post: List<Post>): List<Post>

    suspend fun saveWithAttachment(post: Post, upload: MediaUpload): Post
    suspend fun upload(upload: MediaUpload): Media
}