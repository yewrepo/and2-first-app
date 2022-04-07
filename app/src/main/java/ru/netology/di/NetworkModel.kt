package ru.netology.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.netology.datasource.PostDataSource
import ru.netology.datasource.RetrofitPostSourceImpl
import ru.netology.network.ApiClient
import ru.netology.network.AuthInterceptor
import ru.netology.network.PostAPI
import ru.netology.network.UserAPI
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class NetworkModel {

    @Provides
    fun provideBaseUrl() = ApiClient.BASE_URL


    @Singleton
    @Provides
    fun provideOkHttpClient() = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        .addNetworkInterceptor(AuthInterceptor())
        .build()

    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient, BASE_URL: String): Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .build()

    @Provides
    @Singleton
    fun providePostAPI(retrofit: Retrofit) = retrofit.create(PostAPI::class.java)

    @Provides
    @Singleton
    fun provideUserAPI(retrofit: Retrofit) = retrofit.create(UserAPI::class.java)

    @Provides
    @Singleton
    fun provideRemoteSource(postApi: PostAPI) : PostDataSource = RetrofitPostSourceImpl(postApi)

}