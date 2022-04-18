package ru.netology.datasource

import androidx.lifecycle.asFlow
import androidx.lifecycle.map
import androidx.paging.PagingSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import ru.netology.model.FeedItem
import ru.netology.db.PostEntity
import ru.netology.db.dao.PostDao
import ru.netology.db.fromDto
import ru.netology.db.toDto
import ru.netology.db.toListDto
import ru.netology.model.Media
import ru.netology.model.MediaUpload
import ru.netology.model.Post
import javax.inject.Inject

class RoomPostSourceImpl @Inject constructor(
    private val dao: PostDao
) : PostDataSource {

    override fun get(): Flow<List<Post>> = dao.getAll()
        .map(List<PostEntity>::toListDto)
        .asFlow()
        .flowOn(Dispatchers.Default)

    override fun pagingSource(): PagingSource<Int, FeedItem> {
        return dao.pagingSource().map {
            it.toDto() as FeedItem
        }.asPagingSourceFactory(Dispatchers.IO).invoke()
    }

    override suspend fun getNewer(id: Long): List<Post> {
        return dao.getNewer().map { it.toDto() }
    }

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

    override suspend fun saveWithAttachment(post: Post, upload: MediaUpload): Post {
        TODO("Not yet implemented")
    }

    override suspend fun upload(upload: MediaUpload): Media {
        TODO("Not yet implemented")
    }
}