package ru.netology.db

import android.database.Cursor
import ru.netology.db.dao.PostColumns
import ru.netology.nmedia.Post

fun Cursor.mapPost(): Post {
    return Post(
        id = getInt(getColumnIndexOrThrow(PostColumns.COLUMN_ID)),
        author = getString(getColumnIndexOrThrow(PostColumns.COLUMN_AUTHOR)),
        content = getString(getColumnIndexOrThrow(PostColumns.COLUMN_CONTENT)),
        published = getString(getColumnIndexOrThrow(PostColumns.COLUMN_PUBLISHED)),
        likedByMe = getInt(getColumnIndexOrThrow(PostColumns.COLUMN_LIKED_BY_ME)).toBoolean(),
        likes = getInt(getColumnIndexOrThrow(PostColumns.COLUMN_LIKES)),
        share = getInt(getColumnIndexOrThrow(PostColumns.COLUMN_SHARES))
    )
}

private fun Int.toBoolean(): Boolean = this == 1