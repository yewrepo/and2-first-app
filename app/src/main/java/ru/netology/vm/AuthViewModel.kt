package ru.netology.vm

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.netology.AppAuth
import ru.netology.AuthState
import ru.netology.extension.getOrThrow
import ru.netology.network.UserAPI
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    app: Application,
    private val userAPI: UserAPI,
    private val appAuth: AppAuth,
) : AndroidViewModel(app) {

    val authenticated: Boolean
        get() = appAuth.authStateFlow.value.id != 0L

    val data: LiveData<AuthState> = AppAuth.getInstance()
        .authStateFlow.asLiveData(Dispatchers.Default)

    fun authUser(login: String, pass: String) {
        viewModelScope.launch {
            try {
                val result = userAPI.authentication(login, pass).getOrThrow()
                appAuth.setAuth(result.id, result.token!!)
            } catch (e: Exception) {
                Log.e("qqq", "$e")
            }
        }
    }

}