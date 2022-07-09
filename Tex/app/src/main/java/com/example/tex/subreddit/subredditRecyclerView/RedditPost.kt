package com.example.tex.subreddit.subredditRecyclerView

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

sealed class RedditPost : Parcelable {

    abstract val postId: String
    abstract val nickName: String
    abstract var isFollowed: Boolean
    abstract val url: String
    abstract val prefix: String
    abstract var isSaved: Boolean
    abstract var isLocal: Boolean

    @Parcelize
    data class SimplePost(
        val avatar: String,
        override var nickName: String,
        val time: String,
        val title: String,
        val description: String,
        val comments: String,
        val postType: PostType,
        override val url: String,
        override val postId: String,
        override var isFollowed: Boolean,
        override val prefix: String,
        override var isSaved: Boolean,
        override var isLocal: Boolean
    ) : RedditPost() {
        override fun equals(other: Any?): Boolean {
            if (javaClass != other?.javaClass) {
                return false
            }

            other as SimplePost

            return when {
                avatar != other.avatar -> false
                nickName != other.nickName -> false
                time != other.time -> false
                title != other.title -> false
                comments != other.comments -> false
                url != other.url -> false
                description != other.description -> false
                postId != other.postId -> false
                isFollowed != other.isFollowed -> false
                else -> true
            }
        }
    }

    @Parcelize
    data class VideoPost(
        val avatar: String,
        override var nickName: String,
        val time: String,
        val title: String,
        val comments: String,
        val video: String,
        override val url: String,
        val videoAudio: String,
        val postType: PostType,
        override val postId: String,
        override var isFollowed: Boolean,
        override val prefix: String,
        override var isSaved: Boolean,
        override var isLocal: Boolean
    ) : RedditPost() {
        override fun equals(other: Any?): Boolean {
            if (javaClass != other?.javaClass) {
                return false
            }

            other as VideoPost

            return when {
                avatar != other.avatar -> false
                nickName != other.nickName -> false
                time != other.time -> false
                title != other.title -> false
                comments != other.comments -> false
                url != other.url -> false
                videoAudio != other.videoAudio -> false
                postId != other.postId -> false
                isFollowed != other.isFollowed -> false
                else -> true
            }
        }
    }

    @Parcelize
    data class ImagePost(
        val avatar: String,
        override var nickName: String,
        val time: String,
        val title: String,
        val comments: String,
        val media: List<Pair<String, String>>,
        val thumbnail: String?,
        val postType: PostType,
        override val url: String,
        override val postId: String,
        override var isFollowed: Boolean,
        override val prefix: String,
        override var isSaved: Boolean,
        override var isLocal: Boolean
    ) : RedditPost() {
        override fun equals(other: Any?): Boolean {
            if (javaClass != other?.javaClass) {
                return false
            }

            other as ImagePost

            return when {
                avatar != other.avatar -> false
                nickName != other.nickName -> false
                time != other.time -> false
                title != other.title -> false
                comments != other.comments -> false
                url != other.url -> false
                media != other.media -> false
                postId != other.postId -> false
                isFollowed != other.isFollowed -> false
                else -> true
            }
        }
    }
}
