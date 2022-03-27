package ru.netology.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import ru.netology.datasource.PostDataSource
import ru.netology.nmedia.Post

class RetrofitPostRepositoryImpl(
    private val remoteSource: PostDataSource,
    private val localSource: PostDataSource,
) : PostDataRepository {

    override val data: Flow<List<Post>>
        get() = localSource.get()

    override fun getNewerCount(id: Long): Flow<Int> = flow {
        remoteSource.getNewer(id).let {
            val list = it.map { post ->
                post.copy(
                    isNew = true
                )
            }
            localSource.save(list)
            emit(it.size)
        }
    }.flowOn(Dispatchers.Default)

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