package com.example.tex.DataBase

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [RedditPostDB::class], version = MainDataBase.DB_VERSION, exportSchema = true)
@TypeConverters(Converters::class)
abstract class MainDataBase : RoomDatabase() {

    abstract fun dao(): RedditPostDao

    companion object {
        const val DB_VERSION = 1
        const val DB_NAME = "MainDataBase"
    }
}
