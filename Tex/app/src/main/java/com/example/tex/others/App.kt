package com.example.tex.others

import android.app.Application
import com.example.tex.DataBase.DataBase

class App: Application() {
    override fun onCreate() {
        super.onCreate()
        DataBase.init(this)
    }
}