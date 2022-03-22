package ru.netology.repository

import androidx.lifecycle.LiveData
import ru.netology.datasource.PostDataSource
import ru.netology.nmedia.Post

class RetrofitPostRepositoryImpl(
    private val remoteSource: PostDataSource,
    private val localSource: PostDataSource,
) : PostDataRepository {
    override val data: LiveData<List<Post>>
        get() = localSource.get()

    override suspend fun getAll(): List<Post> {
        remoteSource.getAll().let {
            localSource.save(it)
            return it
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

    override suspend fun save(post: Post) {
        localSource.save(post).apply {
            remoteSource.save(this)
        }
    }

}