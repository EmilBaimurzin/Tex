package com.example.tex.auth

import android.content.Context
import android.util.Log
import com.example.tex.others.PermanentStorage

class AuthorizationRepository(private val context: Context) {
    private val sharedPrefs by lazy {
        context.getSharedPreferences(PermanentStorage.SharedPrefs.AUTH_PREFS,
            Context.MODE_PRIVATE)
    }

    fun toSharedPrefs() {
        Log.e("authorized", "shared")
        sharedPrefs.edit()
            .putBoolean("isAuthorized", true)
            .apply()
    }
}