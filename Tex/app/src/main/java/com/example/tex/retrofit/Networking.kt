package com.example.tex.retrofit

import com.example.tex.others.PermanentStorage
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create

object Networking {
    private val okHttpClient = OkHttpClient.Builder()
        .addNetworkInterceptor(
            CustomInterceptor(PermanentStorage.token)
        )
        .addNetworkInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        .build()

    private val retrofit = Retrofit.Builder()
        .addConverterFactory(MoshiConverterFactory.create())
        .baseUrl("https://oauth.reddit.com")
        .client(okHttpClient)
        .build()


    val mainInterface: MainInterface
        get() = retrofit.create()

}