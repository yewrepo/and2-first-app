package ru.netology.db

object PostColumns {
    const val TABLE = "posts"

    const val COLUMN_ID = "id"
    const val COLUMN_AUTHOR = "author"
    const val COLUMN_CONTENT = "content"
    const val COLUMN_PUBLISHED = "published"
    const val COLUMN_LIKED_BY_ME = "likedByMe"
    const val COLUMN_LIKES = "likes"
    const val COLUMN_SHARES = "shares"

    val ALL_COLUMNS = arrayOf(
        COLUMN_ID,
        COLUMN_AUTHOR,
        COLUMN_CONTENT,
        COLUMN_PUBLISHED,
        COLUMN_LIKED_BY_ME,
        COLUMN_LIKES,
        COLUMN_SHARES,
    )
}