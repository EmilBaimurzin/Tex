package com.example.tex.retrofit

import okhttp3.Interceptor
import okhttp3.Response

class CustomInterceptor(
    private val token: String
): Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        return if (token.isEmpty()) chain.proceed(chain.request())
        else chain.request().newBuilder()
            .addHeader("Authorization", "bearer $token")
            .build()
            .let {
                chain.proceed(it)
            }
    }
}
