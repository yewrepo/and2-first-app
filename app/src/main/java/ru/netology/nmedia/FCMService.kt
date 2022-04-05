package ru.netology.nmedia

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import ru.netology.AppAuth
import ru.netology.notification.Notifications
import java.lang.Exception

class FCMService : FirebaseMessagingService() {

    private var tag = FCMService::class.java.simpleName

    private val content = "content"
    private val gson = Gson()

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.i(tag, "remoteMessage: $remoteMessage")
        remoteMessage.data[content]?.let {
            try {
                gson.fromJson(
                    remoteMessage.data[content],
                    NewPushModel::class.java
                )?.apply {
                    val id = AppAuth.getInstance().authStateFlow.value.id
                    if (recipientId != null) {
                        if ((recipientId == 0L) || (recipientId != 0L && recipientId != id)) {
                            AppAuth.getInstance().sendPushToken()
                        } else if (recipientId == id) {
                            Notifications(applicationContext).notifyText(content)
                        }
                    } else {
                        Notifications(applicationContext).notifyText(content)
                    }
                }
            } catch (e: Exception) {
                Log.e(tag, "$e")
            }
        }
    }

    override fun onNewToken(token: String) {
        Log.i(tag, "token: $token")
        AppAuth.getInstance().sendPushToken(token)
    }
}