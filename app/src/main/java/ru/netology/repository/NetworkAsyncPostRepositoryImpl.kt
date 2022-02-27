package ru.netology.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import ru.netology.nmedia.Post
import java.io.IOException
import java.lang.RuntimeException
import java.util.concurrent.TimeUnit

class NetworkAsyncPostRepositoryImpl : PostAsyncRepository {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        .build()

    private val gson = Gson()
    private val typeToken = object : TypeToken<List<Post>>() {}

    companion object {
        private const val BASE_URL = "http://10.0.2.2:9999"
        private val jsonType = "application/json".toMediaType()
    }

    override fun get(callback: PostListCallback?) {
        val request = Request.Builder()
            .url("${BASE_URL}/api/slow/posts")
            .build()

        return client.newCall(request)
            .enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callback?.onError(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    try {
                        val body = response.body?.string() ?: throw RuntimeException("body is null")
                        callback?.onSuccess(gson.fromJson(body, typeToken.type))
                    } catch (e: Exception) {
                        callback?.onError(e)
                    }
                }
            })
    }

    override fun likeById(id: Int, callback: CompleteCallback?) {
        val request = Request.Builder()
            .post("{}".toRequestBody(jsonType))
            .url("${BASE_URL}/api/posts/$id/likes")
            .build()

        executeResult(request, callback)
    }

    override fun dislikeById(id: Int, callback: CompleteCallback?) {
        val request = Request.Builder()
            .delete()
            .url("${BASE_URL}/api/posts/$id/likes")
            .build()

        executeResult(request, callback)
    }

    override fun removeById(id: Int, callback: CompleteCallback?) {
        val request: Request = Request.Builder()
            .delete()
            .url("${BASE_URL}/api/slow/posts/$id")
            .build()

        execute(request, callback)
    }

    override fun save(post: Post, callback: CompleteCallback?) {
        val request = Request.Builder()
            .post(gson.toJson(post).toRequestBody(jsonType))
            .url("${BASE_URL}/api/slow/posts")
            .build()

        execute(request, callback)
    }

    private fun execute(request: Request, callback: CompleteCallback?) {
        client.newCall(request)
            .enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callback?.onError(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    callback?.onSuccess()
                }
            })
    }

    private fun executeResult(
        request: Request,
        callback: CompleteCallback?
    ) {
        client.newCall(request)
            .enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callback?.onError(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    try {
                        response.body?.string() ?: throw RuntimeException("body is null")
                        callback?.onSuccess()
                    } catch (e: Exception) {
                        callback?.onError(e)
                    }
                }
            })
    }
}