package ru.netology.di

import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.netology.AppAuth
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
    fun provideSharedPref(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences("auth", Context.MODE_PRIVATE)
    }

    @Provides
    fun provideRequestInterceptor(prefs: SharedPreferences) : AuthInterceptor {
        return AuthInterceptor(prefs)
    }

    @Provides
    fun provideBaseUrl() = ApiClient.BASE_URL

    @Singleton
    @Provides
    fun provideOkHttpClient(interceptor: AuthInterceptor) = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        .addNetworkInterceptor(interceptor)
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
    fun provideRemoteSource(postApi: PostAPI): PostDataSource = RetrofitPostSourceImpl(postApi)

}