package com.example.tex.main_fragment

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.util.Log
import com.example.tex.others.PermanentStorage

class MainRepository(private val context: Context) {

    fun isConnectedViaWifi(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        return connectivityManager!!.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)!!.state == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)!!.state == NetworkInfo.State.CONNECTED

    }

    private val sharedPrefs by lazy {
        context.getSharedPreferences(PermanentStorage.SharedPrefs.AUTH_PREFS,
            Context.MODE_PRIVATE)
    }

    fun isAuthorized(action: Boolean): Pair<Boolean, Boolean> {
        val value = sharedPrefs.getBoolean("isAuthorized", false)
        return Pair(action, value)
    }

    fun toSharedPrefs() {
        Log.e("authorized", "shared")
        sharedPrefs.edit()
            .putBoolean("isAuthorized", true)
            .apply()
    }
}