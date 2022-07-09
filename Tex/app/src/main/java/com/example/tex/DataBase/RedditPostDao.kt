package com.example.tex.DataBase

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface RedditPostDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addPost(post: RedditPostDB)

    @Query("SELECT * FROM RedditPost " + "LIMIT :limit OFFSET :offset")
    fun getLocalPosts(limit: Int, offset: Int): List<RedditPostDB>

    @Query("SELECT * FROM RedditPost " + "WHERE postId = :postId")
    fun searchPost(postId: String): RedditPostDB?

    @Query("DELETE FROM RedditPost " + "WHERE postId = :postId")
    fun deletePost(postId: String)
}