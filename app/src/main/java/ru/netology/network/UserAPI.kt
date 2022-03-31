package ru.netology.network

import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import ru.netology.AuthState

interface UserAPI {

    @FormUrlEncoded
    @POST("users/authentication")
    suspend fun authentication(
        @Field("login") login: String,
        @Field("pass") pass: String
    ): Response<AuthState>

    @FormUrlEncoded
    @POST("users/registration")
    suspend fun registration(
        @Field("login") login: String, @Field("pass") pass: String,
        @Field("name") name: String
    ): Response<AuthState>
}