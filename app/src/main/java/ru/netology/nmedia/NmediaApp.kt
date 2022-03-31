package ru.netology.nmedia

import android.app.Application
import ru.netology.AppAuth
import ru.netology.extension.initChannels

class NmediaApp : Application() {

    override fun onCreate() {
        super.onCreate()
        initChannels()
        AppAuth.initApp(applicationContext)
    }
}