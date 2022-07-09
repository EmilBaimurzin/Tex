package com.example.tex.DataBase

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.tex.subreddit.subredditRecyclerView.RedditPost

@Entity(
    tableName = "RedditPost",
    indices = [Index(value = ["postId"], unique = true)]
)
data class RedditPostDB(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val value: RedditPost,
    val postId: String
)