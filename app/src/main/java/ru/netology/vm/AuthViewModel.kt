package ru.netology.vm

import android.app.Application
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.netology.AppAuth
import ru.netology.AuthState
import ru.netology.extension.getOrThrow
import ru.netology.network.UserAPI
import ru.netology.nmedia.Error
import ru.netology.nmedia.Loading
import ru.netology.nmedia.State
import ru.netology.nmedia.Success
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    app: Application,
    private val userAPI: UserAPI,
    private val appAuth: AppAuth,
) : AndroidViewModel(app) {

    val authenticated: Boolean
        get() = appAuth.authStateFlow.value.id != 0L

    val data: LiveData<AuthState> = appAuth
        .authStateFlow.asLiveData(Dispatchers.Default)

    private val _loadingState = MutableLiveData<State>()
    val loadingState: LiveData<State>
        get() = _loadingState

    fun authUser(login: String, pass: String) {
        _loadingState.postValue(Loading)
        viewModelScope.launch {
            try {
                val result = userAPI.authentication(login, pass).getOrThrow()
                appAuth.setAuth(result.id, result.token!!)
                _loadingState.postValue(Success)
            } catch (e: Exception) {
                _loadingState.postValue(Error)
            }
        }
    }

    fun removeAuth() {
        appAuth.removeAuth()
        _loadingState.postValue(Success)
    }

}