package ru.netology.repository

import ru.netology.nmedia.Post

interface PostAsyncRepository {
    fun get(callback: PostListCallback)
    fun likeById(id: Int, callback: CompleteCallback? = null)
    fun dislikeById(id: Int, callback: CompleteCallback? = null)
    fun removeById(id: Int, callback: CompleteCallback? = null)
    fun save(post: Post, callback: CompleteCallback? = null)
}

interface PostListCallback {
    fun onSuccess(list: List<Post>)
    fun onError(e: Throwable)
}

interface CompleteCallback {
    fun onSuccess()
    fun onError(e: Throwable)
}