package ru.netology.db

import ru.netology.nmedia.Attachment
import ru.netology.nmedia.Post

fun PostEntity.toDto(): Post {
    return Post(
        id = id,
        author = author,
        authorId = authorId,
        authorAvatar = authorAvatar,
        content = content,
        published = published,
        likedByMe = likedByMe,
        likes = likes,
        share = share,
        youtubeLink = youtubeLink,
        isNew = isNew,
        ownedByMe = ownedByMe,
        attachment = attachment.toDto()
    )
}

fun Post.fromDto(): PostEntity {
    return PostEntity(
        id = id,
        author = author,
        authorId = authorId,
        authorAvatar = authorAvatar,
        content = content,
        published = published,
        likedByMe = likedByMe,
        likes = likes,
        share = share,
        youtubeLink = youtubeLink,
        isNew = isNew,
        ownedByMe = ownedByMe,
        attachment = attachment.fromDto()
    )
}

private fun AttachmentLocal?.toDto(): Attachment? {
    return this?.let {
        Attachment(it.url, it.description, it.type)
    }
}


private fun Attachment?.fromDto(): AttachmentLocal? {
    return this?.let {
        AttachmentLocal(it.url, it.description, it.type)
    }
}

fun List<Post>.toEntity(): List<PostEntity> = map(Post::fromDto)
fun List<PostEntity>.toListDto(): List<Post> = map(PostEntity::toDto)
