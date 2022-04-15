package ru.netology.datasource

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okio.IOException
import ru.netology.extension.getOrThrow
import ru.netology.network.*
import ru.netology.nmedia.*
import javax.inject.Inject

class RetrofitPostSourceImpl @Inject constructor(
    private val api: PostAPI
) : PostDataSource {

    override fun get(): Flow<List<Post>> = flow { }

    override suspend fun getNewer(id: Long): List<Post> {
        return handleError {
            return@handleError api
                .getNewer(id)
                .getOrThrow()
        }
    }

    override suspend fun getAll(): List<Post> {
        return handleError {
            return@handleError api.getAll().getOrThrow()
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

    override suspend fun saveWithAttachment(post: Post, upload: MediaUpload): Post {
        try {
            val media = upload(upload)
            val postWithAttachment =
                post.copy(attachment = Attachment(url = media.id, "", AttachmentType.IMAGE))
            return save(postWithAttachment)
        } catch (e: AppError) {
            throw e
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun upload(upload: MediaUpload): Media {
        try {
            val media = MultipartBody.Part.createFormData(
                "file", upload.file.name, upload.file.asRequestBody()
            )
            val response = api.upload(media)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            return response.body() ?: throw ApiError(response.code(), response.message())
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
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

}