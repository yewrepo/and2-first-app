package ru.netology.db

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val author: String?,
    val authorAvatar: String?,
    val content: String?,
    val published: Long,
    val likedByMe: Boolean = false,
    val likes: Int = 0,
    val share: Int = 0,
    val view: Int = 0,
    val youtubeLink: String? = null,
    val isNew: Boolean = false,
    @Embedded
    var attachment: AttachmentLocal?,
)