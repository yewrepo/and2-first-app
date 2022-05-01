package ru.netology

import android.content.Context
import android.util.Log
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.netology.network.UserAPI
import ru.netology.model.PushToken
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppAuth @Inject constructor(
    @ApplicationContext context: Context,
    private val userAPI: UserAPI
) {
    private val tag = AppAuth::class.java.simpleName
    private val prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)

    private val _authStateFlow: MutableStateFlow<AuthState>

    init {
        val id = prefs.getLong(idKey, 0)
        val token = prefs.getString(tokenKey, null)

        if (id == 0L || token == null) {
            _authStateFlow = MutableStateFlow(AuthState())
            with(prefs.edit()) {
                clear()
                apply()
            }
        } else {
            _authStateFlow = MutableStateFlow(AuthState(id, token))
        }

        sendPushToken()
    }

    val authStateFlow: StateFlow<AuthState> = _authStateFlow.asStateFlow()

    @Synchronized
    fun setAuth(id: Long, token: String) {
        _authStateFlow.value = AuthState(id, token)
        with(prefs.edit()) {
            putLong(idKey, id)
            putString(tokenKey, token)
            commit()
        }
        sendPushToken()
    }

    @Synchronized
    fun removeAuth() {
        _authStateFlow.value = AuthState()
        with(prefs.edit()) {
            clear()
            commit()
        }
        sendPushToken()
    }

    fun sendPushToken(token: String? = null) {
        Log.i(tag, "$token")
        CoroutineScope(Dispatchers.Default).launch {
            try {
                if (token != null) {
                    userAPI.save(PushToken(token))
                } else {
                    Firebase.messaging.token.addOnSuccessListener { token ->
                        CoroutineScope(Dispatchers.Default).launch {
                            try {
                                Log.i(tag, "$token")
                                userAPI.save(PushToken(token))
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    companion object {

        const val idKey = "id"
        const val tokenKey = "token"

        @Volatile
        private var instance: AppAuth? = null

        fun getInstance(): AppAuth = synchronized(this) {
            instance ?: throw IllegalStateException(
                "AppAuth is not initialized, you must call AppAuth.initializeApp(Context context) first."
            )
        }

        fun initApp(context: Context, userAPI: UserAPI): AppAuth = instance ?: synchronized(this) {
            instance ?: buildAuth(context, userAPI).also { instance = it }
        }

        private fun buildAuth(context: Context, userAPI: UserAPI): AppAuth =
            AppAuth(context, userAPI)
    }
}

data class AuthState(val id: Long = 0, val token: String? = null, val avatar: String? = null)