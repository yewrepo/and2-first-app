package ru.netology.network

import retrofit2.http.*
import ru.netology.nmedia.Post

interface PostAPI {

    @GET("posts")
    suspend fun getAll(): List<Post>

    @GET("posts/{id}")
    suspend fun getById(@Path("id") id: Int): Post

    @DELETE("posts/{id}")
    suspend fun removeById(@Path("id") id: Int)

    @POST("posts")
    suspend fun save(@Body post: Post): Post

    @POST("posts/{id}/likes")
    suspend fun likeById(@Path("id") id: Int): Post

    @DELETE("posts/{id}/likes")
    suspend fun dislikeById(@Path("id") id: Int): Post
}