package ru.netology.repository

import ru.netology.datasource.PostDataSource
import ru.netology.nmedia.Post

class RetrofitPostRepositoryImpl(
    private val remoteSource: PostDataSource,
    private val localSource: PostDataSource,
) : PostDataRepository {

    override suspend fun getAll(forced: Boolean): List<Post> {
        return if (forced) {
            localSource.save(remoteSource.getAll())
        } else {
            localSource.getAll()
        }
    }

    override suspend fun likeById(id: Long): Post {
        return localSource.likeById(id).let {
            return@let remoteSource.likeById(id)
        }
    }

    override suspend fun dislikeById(id: Long): Post {
        return localSource.dislikeById(id).let {
            return@let remoteSource.dislikeById(id)
        }
    }

    override suspend fun removeById(id: Long) {
        return localSource.removeById(id).let {
            return@let remoteSource.removeById(id)
        }
    }

    override suspend fun save(post: Post): Post {
        return remoteSource.save(localSource.save(post))
    }

}