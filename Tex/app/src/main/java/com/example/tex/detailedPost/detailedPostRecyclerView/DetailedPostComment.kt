package com.example.tex.detailedPost.detailedPostRecyclerView

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DetailedPostComment(
    val nickname: String,
    val time: String,
    val description: String,
    val commentId: String,
    var moreCommentsIds: List<String>,
    var parentId: String?,
    var ups: Int,
    var likes: Boolean?,
    var isSaved: Boolean,
    var elementVisibility: Boolean
): Parcelable
