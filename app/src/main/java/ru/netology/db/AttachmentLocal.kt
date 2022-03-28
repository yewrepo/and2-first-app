package ru.netology.db

import ru.netology.nmedia.AttachmentType

data class AttachmentLocal(
    var url: String,
    val description: String,
    var type: AttachmentType,
)