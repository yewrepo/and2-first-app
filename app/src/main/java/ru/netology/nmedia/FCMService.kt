package ru.netology.nmedia

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import ru.netology.notification.Notifications
import java.lang.Exception

class FCMService : FirebaseMessagingService() {

    private var tag = FCMService::class.java.simpleName

    private val action = "action"
    private val content = "content"
    private val gson = Gson()

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.i(tag, "remoteMessage: $remoteMessage")
        remoteMessage.data[action]?.let {
            try {
                when (Action.valueOf(it)) {
                    Action.LIKE -> handleLike(
                        gson.fromJson(
                            remoteMessage.data[content],
                            Like::class.java
                        )
                    )
                }
            } catch (e: Exception) {
                Log.e(tag, "$e")
            }
        }
    }

    override fun onNewToken(token: String) {
        Log.i(tag, "token: $token")
    }

    private fun handleLike(content: Like) {
        Notifications(applicationContext).handleLike(content)
    }
}