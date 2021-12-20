package ru.netology.vm

import androidx.lifecycle.ViewModel
import ru.netology.repository.PostRepository
import ru.netology.repository.PostRepositoryMemoryImpl

class PostViewModel : ViewModel() {

    private val repository: PostRepository = PostRepositoryMemoryImpl()
    val data = repository.get()

    fun likeById(id: Int) = repository.likeById(id)
    fun shareById(id: Int) = repository.shareById(id)
}
