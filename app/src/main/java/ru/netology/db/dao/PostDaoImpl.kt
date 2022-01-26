package ru.netology.db.dao

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import ru.netology.db.PostColumns
import ru.netology.db.mapPost
import ru.netology.nmedia.Post

class PostDaoImpl(
    private val db: SQLiteDatabase
) : PostDao {
    override fun getAll(): List<Post> {
        val posts = mutableListOf<Post>()
        db.query(
            PostColumns.TABLE,
            PostColumns.ALL_COLUMNS,
            null,
            null,
            null,
            null,
            "${PostColumns.COLUMN_ID} DESC"
        ).use {
            while (it.moveToNext()) {
                posts.add(it.mapPost())
            }
        }
        return posts
    }

    override fun likeById(id: Int) {
        db.execSQL(
            """
               UPDATE ${PostColumns.TABLE} SET
                likes = likes + CASE WHEN likedByMe THEN -1 ELSE 1 END,
                likedByMe = CASE WHEN  likedByMe THEN 0 ELSE 1 END
                WHERE id = ?
            """
                .trimIndent(), arrayOf(id)
        )
    }

    override fun shareById(id: Int) {
        db.execSQL(
            """
               UPDATE ${PostColumns.TABLE} SET
                shares = shares + 1
                WHERE id = ?
            """
                .trimIndent(), arrayOf(id)
        )
    }

    override fun removeById(id: Int) {
        db.delete(
            PostColumns.TABLE,
            "${PostColumns.COLUMN_ID} = ?",
            arrayOf(id.toString())
        )
    }

    override fun save(post: Post): Post {
        val values = ContentValues().apply {
            if (post.id != 0) {
                put(PostColumns.COLUMN_ID, post.id)
            }
            put(PostColumns.COLUMN_AUTHOR, "Me")
            put(PostColumns.COLUMN_CONTENT, post.content)
            put(PostColumns.COLUMN_PUBLISHED, "Now")
        }
        val id = db.replace(PostColumns.TABLE, null, values)
        db.query(
            PostColumns.TABLE,
            PostColumns.ALL_COLUMNS,
            "${PostColumns.COLUMN_ID} = ?",
            arrayOf(id.toString()),
            null,
            null,
            null
        ).use {
            it.moveToNext()
            return it.mapPost()
        }
    }

    companion object {

        val DLLs = """
            CREATE TABLE ${PostColumns.TABLE}(
            ${PostColumns.COLUMN_ID} INTEGER PRIMARY KEY AUTOINCREMENT,
            ${PostColumns.COLUMN_AUTHOR} TEXT NOT NULL,
            ${PostColumns.COLUMN_CONTENT} TEXT NOT NULL,
            ${PostColumns.COLUMN_PUBLISHED} TEXT NOT NULL,
            ${PostColumns.COLUMN_LIKED_BY_ME} BOOLEAN NOT NULL DEFAULT 0,
            ${PostColumns.COLUMN_LIKES} INTEGER NOT NULL DEFAULT 0,
            ${PostColumns.COLUMN_SHARES} INTEGER NOT NULL DEFAULT 0
            )
        """.trimIndent()

    }
}