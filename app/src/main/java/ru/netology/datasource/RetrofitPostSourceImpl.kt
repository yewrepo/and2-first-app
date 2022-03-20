package ru.netology.datasource

import okio.IOException
import retrofit2.Response
import ru.netology.network.ApiError
import ru.netology.network.NetworkError
import ru.netology.network.PostAPI
import ru.netology.network.UnknownError
import ru.netology.nmedia.Post

class RetrofitPostSourceImpl(
    private val api: PostAPI
) : PostDataSource {

    override suspend fun getAll(): List<Post> {
        return handleError {
            val response = api.getAll()
            return@handleError response.getOrThrow()
        }
    }

    override suspend fun likeById(id: Long): Post {
        return handleError {
            return@handleError api.likeById(id).getOrThrow()
        }
    }

    override suspend fun dislikeById(id: Long): Post {
        return handleError {
            return@handleError api.dislikeById(id).getOrThrow()
        }
    }

    override suspend fun removeById(id: Long) {
        return handleError {
            return@handleError api.removeById(id).getOrThrow()
        }
    }

    override suspend fun save(post: Post): Post {
        return handleError {
            return@handleError api.save(post).getOrThrow()
        }
    }

    override suspend fun save(post: List<Post>): List<Post> {
        throw IllegalStateException("Not use here")
    }

    private suspend fun <T> handleError(block: suspend () -> T): T {
        try {
            return block()
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    private fun <T> Response<T>.getOrThrow(): T {
        if (isSuccessful.not()) {
            throw ApiError(code(), message())
        }
        return body() ?: throw ApiError(
            code(),
            message()
        )
    }
}