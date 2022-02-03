package ru.netology.nmedia

enum class Action {
    LIKE,
    NEW_POST
}

data class Like(
    val userId: Int,
    val userName: String,
    val postId: Int,
    val postAuthor: String
)

data class NewPost(
    val postId: Int,
    val postAuthor: String,
    val content: String,
    val contentLarge: String
)