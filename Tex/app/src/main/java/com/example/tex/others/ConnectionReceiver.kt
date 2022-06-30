package com.example.tex.others

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager

class ConnectionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent?.action
        if (action.equals(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION)) {
            PermanentStorage.connection =
                intent!!.getBooleanExtra(WifiManager.EXTRA_SUPPLICANT_CONNECTED, false)
        }
    }
}
