package ru.netology.nmedia

data class Post(
    val id: Int,
    val author: String,
    val content: String,
    val published: String,
    val likedByMe: Boolean = false,
    val likes: Int = 0,
    val share: Int = 0,
    val view: Int = 0,
    val youtubeLink: String? = null
)
