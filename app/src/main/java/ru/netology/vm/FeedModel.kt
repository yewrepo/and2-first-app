package ru.netology.vm

import ru.netology.nmedia.Post

data class FeedModel(
    val posts: List<Post> = emptyList(),
    val loading: Boolean = false,
    val error: Boolean = false,
    val errorDescription: ErrorData? = null,
    val empty: Boolean = false
)
