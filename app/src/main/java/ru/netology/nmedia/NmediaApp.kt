package ru.netology.nmedia

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import ru.netology.AppAuth
import ru.netology.extension.initChannels
import ru.netology.network.UserAPI
import javax.inject.Inject

@HiltAndroidApp
class NmediaApp : Application() {

    @Inject
    lateinit var userAPI: UserAPI

    override fun onCreate() {
        super.onCreate()
        initChannels()
        AppAuth.initApp(applicationContext, userAPI)
    }
}