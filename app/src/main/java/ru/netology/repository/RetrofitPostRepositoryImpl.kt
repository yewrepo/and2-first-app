package ru.netology.repository

import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import ru.netology.network.ApiClient
import ru.netology.nmedia.Post

class RetrofitPostRepositoryImpl : PostAsyncRepository {
    override fun get(callback: PostListCallback?) {
        ApiClient.retrofitService.getAll().enqueue(object : Callback<List<Post>> {
            override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        callback?.onSuccess(it)
                    }
                } else {
                    callback?.onError(HttpException(response))
                }
            }

            override fun onFailure(call: Call<List<Post>>, t: Throwable) {
                callback?.onError(t)
            }
        })
    }

    override fun likeById(id: Int, callback: CompleteCallback?) {
        ApiClient.retrofitService.likeById(id).enqueue(object : Callback<Post> {
            override fun onResponse(call: Call<Post>, response: Response<Post>) {
                proceedPostResponse(response, callback)
            }

            override fun onFailure(call: Call<Post>, t: Throwable) {
                callback?.onError(t)
            }
        })
    }

    override fun dislikeById(id: Int, callback: CompleteCallback?) {
        ApiClient.retrofitService.dislikeById(id).enqueue(object : Callback<Post> {
            override fun onResponse(call: Call<Post>, response: Response<Post>) {
                proceedPostResponse(response, callback)
            }

            override fun onFailure(call: Call<Post>, t: Throwable) {
                callback?.onError(t)
            }
        })
    }

    override fun removeById(id: Int, callback: CompleteCallback?) {
        ApiClient.retrofitService.removeById(id).enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                proceedResponse(response, callback)
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                callback?.onError(t)
            }
        })
    }

    override fun save(post: Post, callback: CompleteCallback?) {
        ApiClient.retrofitService.save(post).enqueue(object : Callback<Post> {
            override fun onResponse(call: Call<Post>, response: Response<Post>) {
                proceedPostResponse(response, callback)
            }

            override fun onFailure(call: Call<Post>, t: Throwable) {
                callback?.onError(t)
            }
        })
    }

    private fun proceedResponse(
        response: Response<Unit>,
        callback: CompleteCallback?
    ) {
        if (response.isSuccessful) {
            response.body()?.let {
                callback?.onSuccess()
            }
        } else {
            callback?.onError(HttpException(response))
        }
    }

    private fun proceedPostResponse(
        response: Response<Post>,
        callback: CompleteCallback?
    ) {
        if (response.isSuccessful) {
            response.body()?.let {
                callback?.onSuccess()
            }
        } else {
            callback?.onError(HttpException(response))
        }
    }
}