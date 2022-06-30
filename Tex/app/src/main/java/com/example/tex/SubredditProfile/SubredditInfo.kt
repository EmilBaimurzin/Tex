package com.example.tex.SubredditProfile

data class SubredditInfo(
    val name: String,
    var isFriend: Boolean,
    var isFollowed: Boolean,
    val avatar: String,
    val karma: Long
)
