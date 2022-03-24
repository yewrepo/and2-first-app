package ru.netology.datasource

import androidx.lifecycle.asFlow
import androidx.lifecycle.map
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import ru.netology.db.PostEntity
import ru.netology.db.dao.PostDao
import ru.netology.db.fromDto
import ru.netology.db.toDto
import ru.netology.db.toListDto
import ru.netology.nmedia.Post

class RoomPostSourceImpl(
    private val dao: PostDao
) : PostDataSource {
    override fun get(): Flow<List<Post>> = dao.getAll()
        .map(List<PostEntity>::toListDto)
        .asFlow()
        .flowOn(Dispatchers.Default)

    override suspend fun getAll(): List<Post> {
        return dao.getAll().value?.let { list ->
            return@let list.toListDto()
        } ?: emptyList()
    }

    override suspend fun likeById(id: Long): Post {
        dao.likeById(id)
        return dao.getById(id).toDto()
    }

    override suspend fun dislikeById(id: Long): Post {
        dao.likeById(id)
        return dao.getById(id).toDto()
    }

    override suspend fun removeById(id: Long) = dao.removeById(id)

    override suspend fun save(post: Post): Post {
        val id = dao.save(post.fromDto())
        return dao.getById(id).toDto()
    }

    override suspend fun save(post: List<Post>): List<Post> {
        dao.insert(post.map(Post::fromDto))
        return dao.getAll().value?.map { it.toDto() } ?: emptyList()
    }
}