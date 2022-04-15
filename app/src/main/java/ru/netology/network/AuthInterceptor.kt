package ru.netology.network

import android.content.SharedPreferences
import okhttp3.Interceptor
import okhttp3.Response
import ru.netology.AppAuth

class AuthInterceptor(
    var prefs: SharedPreferences
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        prefs.getString(AppAuth.tokenKey, null).let { token ->
            if (token == null) {
                chain.request().newBuilder().build()
            } else {
                chain.request().newBuilder()
                    .addHeader("Authorization", token)
                    .build()
            }.also {
                return chain.proceed(it)
            }
        }
    }
}
