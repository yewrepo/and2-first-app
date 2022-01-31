package ru.netology.nmedia

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FCMService : FirebaseMessagingService() {

    var tag = FCMService::class.java.simpleName

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.i(tag, "remoteMessage: $remoteMessage")
    }

    override fun onNewToken(token: String) {
        Log.i(tag, "token: $token")
    }

}