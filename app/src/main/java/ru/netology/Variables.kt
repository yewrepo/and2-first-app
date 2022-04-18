package ru.netology

import okhttp3.MediaType.Companion.toMediaType

object Variables {
    val BASE_URL = "http://10.0.2.2:9999/api/"
    val jsonType = "application/json".toMediaType()
}