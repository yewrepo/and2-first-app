package ru.netology.repository

import ru.netology.nmedia.Post

interface PostDataRepository {
    suspend fun getAll(forced: Boolean = false): List<Post>
    suspend fun likeById(id: Long): Post
    suspend fun dislikeById(id: Long): Post
    suspend fun removeById(id: Long)
    suspend fun save(post: Post): Post
}