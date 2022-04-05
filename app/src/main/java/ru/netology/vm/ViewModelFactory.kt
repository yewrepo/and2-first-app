package ru.netology.vm

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.netology.AppAuth
import ru.netology.network.UserAPI
import ru.netology.repository.PostDataRepository

class ViewModelFactory(
    private val app: Application,
    private val repository: PostDataRepository,
    private val userAPI: UserAPI,
    private val appAuth: AppAuth
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(PostViewModel::class.java) -> {
                PostViewModel(app, repository) as T
            }
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
                AuthViewModel(app, userAPI, appAuth) as T
            }
            else -> throw IllegalStateException("")
        }
    }
}