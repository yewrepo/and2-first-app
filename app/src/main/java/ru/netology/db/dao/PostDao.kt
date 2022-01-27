package ru.netology.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import ru.netology.db.PostEntity
import ru.netology.nmedia.Post

@Dao
interface PostDao {

    @Query("SELECT * FROM PostEntity ORDER BY id")
    fun getAll(): LiveData<List<Post>>

    @Query("UPDATE PostEntity SET likes = likes + CASE WHEN likedByMe THEN -1 ELSE 1 END, likedByMe = CASE WHEN  likedByMe THEN 0 ELSE 1 END WHERE id = :id")
    fun likeById(id: Int)

    @Query("UPDATE PostEntity SET share = share + 1 WHERE id = :id")
    fun shareById(id: Int)

    @Query("DELETE FROM PostEntity WHERE id = :id")
    fun removeById(id: Int)

    @Query("UPDATE PostEntity SET content = :content, youtubeLink =:youtubeLink WHERE id = :id")
    fun updateContentById(id: Int, content: String, youtubeLink: String)

    fun save(post: PostEntity) =
        if (post.id == 0) insert(post) else
            updateContentById(
                post.id,
                post.content ?: "",
                post.youtubeLink ?: ""
            )

    @Insert
    fun insert(post: PostEntity)
}