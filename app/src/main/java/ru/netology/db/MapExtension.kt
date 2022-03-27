package ru.netology.db

import ru.netology.nmedia.Post

fun PostEntity.toDto(): Post {
    return Post(
        id = id,
        author = author,
        content = content,
        published = published,
        likedByMe = likedByMe,
        likes = likes,
        share = share,
        youtubeLink = youtubeLink,
        isNew = isNew
    )
}

fun Post.fromDto(): PostEntity {
    return PostEntity(
        id = id,
        author = author,
        content = content,
        published = published,
        likedByMe = likedByMe,
        likes = likes,
        share = share,
        youtubeLink = youtubeLink,
        isNew = isNew
    )
}

fun List<Post>.toEntity(): List<PostEntity> = map(Post::fromDto)
fun List<PostEntity>.toListDto(): List<Post> = map(PostEntity::toDto)
