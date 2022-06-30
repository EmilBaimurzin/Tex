package com.example.tex.SubredditProfile.subredditProfileRecyclerView

import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.tex.databinding.ItemUserCommentBinding

sealed class SubredditCommentsRVHolder(binding: ViewBinding) : RecyclerView.ViewHolder(binding.root) {

    class CommentsViewHolder(private val binding: ItemUserCommentBinding) :
        SubredditCommentsRVHolder(binding) {
        fun bind(item: SubredditComment) {
            binding.apply {
                postTextView.text = item.postTitle
                communityTextView.text = item.community
                selftextTextView.text = item.selfText
            }
        }
    }
}