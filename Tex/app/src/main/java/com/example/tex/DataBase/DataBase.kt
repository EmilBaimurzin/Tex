package com.example.tex.DataBase

import android.content.Context
import androidx.room.Room

object DataBase {
    lateinit var instance: MainDataBase
        private set

    fun init(context: Context) {
        instance = Room.databaseBuilder(
            context,
            MainDataBase::class.java,
            MainDataBase.DB_NAME
        )
            .build()
    }
}
