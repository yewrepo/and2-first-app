package ru.netology.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import ru.netology.nmedia.Like
import ru.netology.nmedia.R
import kotlin.random.Random

class Notifications(
    private val c: Context
) {

    private val channelId = "Nmedia_channel"

    fun initChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = c.getString(R.string.channel_remote_name)
            val descriptionText = c.getString(R.string.channel_remote_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }
            val manager = c.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    fun handleLike(content: Like) {
        val notification = NotificationCompat.Builder(c, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(
                c.getString(R.string.notification_user_like, content.userName, content.postAuthor)
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        NotificationManagerCompat.from(c).notify(Random.nextInt(100_000), notification)
    }
}