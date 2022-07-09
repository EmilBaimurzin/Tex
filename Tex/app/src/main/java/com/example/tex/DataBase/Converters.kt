package com.example.tex.DataBase

import androidx.room.TypeConverter
import com.example.tex.subreddit.subredditRecyclerView.PostType
import com.example.tex.subreddit.subredditRecyclerView.RedditPost
import com.google.gson.Gson
import org.json.JSONObject

object Converters {

    @TypeConverter
    fun sealedClassToString(sealedClass: RedditPost): String = Gson().toJson(sealedClass)

    @TypeConverter
    fun sealedClassFromString(sealedClass: String): RedditPost? {
        return when (JSONObject(sealedClass).getString("postType")) {
            PostType.SimplePost.toString() -> {
                Gson().fromJson(sealedClass, RedditPost.SimplePost::class.java)
            }
            PostType.ImagePost.toString() -> {
                Gson().fromJson(sealedClass, RedditPost.ImagePost::class.java)
            }
            PostType.VideoPost.toString() -> {
                Gson().fromJson(sealedClass, RedditPost.VideoPost::class.java)
            }
            else -> { null }
        }
    }
}