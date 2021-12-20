package ru.netology.vm

import androidx.lifecycle.ViewModel
import ru.netology.repository.PostRepository
import ru.netology.repository.PostRepositoryMemoryImpl

class PostViewModel : ViewModel() {

    private val repository: PostRepository = PostRepositoryMemoryImpl()
    val data = repository.get()

    fun like() = repository.like()
    fun share() = repository.share()
}
