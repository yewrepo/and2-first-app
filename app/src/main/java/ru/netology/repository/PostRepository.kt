package ru.netology.repository

import androidx.lifecycle.LiveData
import ru.netology.nmedia.Post

interface PostRepository {
    fun get(): LiveData<List<Post>>
    fun likeById(id: Int)
    fun shareById(id: Int)
}