package ru.netology

import okhttp3.MediaType.Companion.toMediaType

object Variables {
    const val BASE_URL = "http://10.0.2.2:9999/api/"
    const val BASE_MEDIA_URL = "http://10.0.2.2:9999/media/"
    const val BASE_AVATARS_URL = "http://10.0.2.2:9999/avatars/"
    val jsonType = "application/json".toMediaType()
}