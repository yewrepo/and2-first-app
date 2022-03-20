package ru.netology.datasource

import ru.netology.nmedia.Post

interface PostDataSource {
    suspend fun getAll(): List<Post>
    suspend fun likeById(id: Long): Post
    suspend fun dislikeById(id: Long): Post
    suspend fun removeById(id: Long)
    suspend fun save(post: Post): Post
    suspend fun save(post: List<Post>): List<Post>
}