package ru.netology.network

import okhttp3.Interceptor
import okhttp3.Response
import ru.netology.AppAuth

class AuthInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        AppAuth.getInstance().authStateFlow.value.token?.let { token ->
            val request = chain.request().newBuilder()
                .addHeader("Authorization", token)
                .build()
            return chain.proceed(request)
        }
        return chain.proceed(chain.request())
    }
}
