package ru.netology.vm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.Dispatchers
import ru.netology.AppAuth
import ru.netology.AuthState

class AuthViewModel(
    app: Application,
) : AndroidViewModel(app) {

    val authenticated: Boolean
        get() = AppAuth.getInstance().authStateFlow.value.id != 0L

    val data: LiveData<AuthState> = AppAuth.getInstance()
        .authStateFlow.asLiveData(Dispatchers.Default)

}