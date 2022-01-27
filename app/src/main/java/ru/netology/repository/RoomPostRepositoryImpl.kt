package ru.netology.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import ru.netology.db.dao.PostDao
import ru.netology.db.toEntity
import ru.netology.nmedia.Post

class RoomPostRepositoryImpl(
    private val dao: PostDao
) : PostRepository {

    override fun get(): LiveData<List<Post>> = Transformations.map(dao.getAll()) { list ->
        list.map { post ->
            Post(
                id = post.id,
                content = post.content,
                published = post.published,
                author = post.author,
                likedByMe = post.likedByMe,
                share = post.share,
                likes = post.likes,
                youtubeLink = post.youtubeLink
            )
        }
    }

    override fun likeById(id: Int) {
        dao.likeById(id)
    }

    override fun shareById(id: Int) {
        dao.shareById(id)
    }

    override fun removeById(id: Int) {
        dao.removeById(id)
    }

    override fun save(post: Post) {
        dao.save(post.toEntity())
    }
}