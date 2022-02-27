package ru.netology.network

import retrofit2.Call
import retrofit2.http.*
import ru.netology.nmedia.Post

interface PostAPI {

    @GET("posts")
    fun getAll(): Call<List<Post>>

    @GET("posts/{id}")
    fun getById(@Path("id") id: Int): Call<Post>

    @DELETE("posts/{id}")
    fun removeById(@Path("id") id: Int): Call<Unit>

    @POST("posts")
    fun save(@Body post: Post): Call<Post>

    @POST("posts/{id}/likes")
    fun likeById(@Path("id") id: Int): Call<Post>

    @DELETE("posts/{id}/likes")
    fun dislikeById(@Path("id") id: Int): Call<Post>
}