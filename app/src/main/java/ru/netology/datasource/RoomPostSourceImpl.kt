package ru.netology.datasource

import ru.netology.db.PostEntity
import ru.netology.db.dao.PostDao
import ru.netology.db.fromDto
import ru.netology.db.toDto
import ru.netology.nmedia.Post

class RoomPostSourceImpl(
    private val dao: PostDao
) : PostDataSource {

    override suspend fun getAll(): List<Post> {
        return dao.getAll().toDto()
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
        return post.copy(id = id)
    }

    override suspend fun save(post: List<Post>): List<Post> {
        dao.insert(post.map(Post::fromDto))
        return dao.getAll().map(PostEntity::toDto)
    }
}