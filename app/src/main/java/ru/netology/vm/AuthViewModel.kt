package ru.netology.vm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.netology.AppAuth
import ru.netology.AuthState
import ru.netology.extension.getOrThrow
import ru.netology.network.ApiClient

class AuthViewModel(
    app: Application,
) : AndroidViewModel(app) {

    val authenticated: Boolean
        get() = AppAuth.getInstance().authStateFlow.value.id != 0L

    val data: LiveData<AuthState> = AppAuth.getInstance()
        .authStateFlow.asLiveData(Dispatchers.Default)

    fun authUser(login: String, pass: String) {
        viewModelScope.launch {
            try {
                val result = ApiClient.userService.authentication(login, pass)
                    .getOrThrow()
                AppAuth.getInstance().setAuth(result.id, result.token!!)
            } catch (e: Exception) {
            }
        }
    }

}