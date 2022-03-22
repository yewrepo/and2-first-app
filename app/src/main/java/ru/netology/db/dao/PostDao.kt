package ru.netology.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.netology.db.PostEntity

@Dao
interface PostDao {

    @Query("SELECT * FROM PostEntity ORDER BY id")
    fun getAll(): LiveData<List<PostEntity>>

    @Query("UPDATE PostEntity SET likes = likes + CASE WHEN likedByMe THEN -1 ELSE 1 END, likedByMe = CASE WHEN  likedByMe THEN 0 ELSE 1 END WHERE id = :id")
    suspend fun likeById(id: Long)

    @Query("UPDATE PostEntity SET share = share + 1 WHERE id = :id")
    suspend fun shareById(id: Long)

    @Query("DELETE FROM PostEntity WHERE id = :id")
    suspend fun removeById(id: Long)

    @Query("UPDATE PostEntity SET content = :content, youtubeLink =:youtubeLink WHERE id = :id")
    suspend fun updateContentById(id: Long, content: String, youtubeLink: String)

    @Query("SELECT * FROM PostEntity WHERE id = :id")
    suspend fun getById(id: Long): PostEntity

    suspend fun save(post: PostEntity): Long {
        return if (post.id == 0L) {
            insert(post)
        } else {
            updateContentById(
                post.id,
                post.content ?: "",
                post.youtubeLink ?: ""
            )
            post.id
        }
    }


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(post: PostEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(post: List<PostEntity>)
}