package ru.netology.vm

import ru.netology.nmedia.Post

data class FeedModel(
    val posts: List<Post> = emptyList(),
    val empty: Boolean = false
)
