package ru.netology.db

import ru.netology.model.AttachmentType

data class AttachmentLocal(
    var url: String,
    val description: String,
    var type: AttachmentType,
)