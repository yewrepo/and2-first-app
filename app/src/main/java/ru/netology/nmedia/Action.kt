package ru.netology.nmedia

enum class Action {
    LIKE
}

data class Like(
    val userId: Int,
    val userName: String,
    val postId: Int,
    val postAuthor: String
)