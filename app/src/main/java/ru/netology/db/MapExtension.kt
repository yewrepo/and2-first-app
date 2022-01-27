package ru.netology.db

import ru.netology.nmedia.Post

fun PostEntity.mapPost(): Post {
    return Post(
        id = id,
        author = author,
        content = content,
        published = published,
        likedByMe = likedByMe,
        likes = likes,
        share = share,
        youtubeLink = youtubeLink
    )
}

fun Post.toEntity(): PostEntity {
    return PostEntity(
        id = id,
        author = author,
        content = content,
        published = published,
        likedByMe = likedByMe,
        likes = likes,
        share = share,
        youtubeLink = youtubeLink
    )
}
