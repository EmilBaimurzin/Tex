package com.example.tex.SubredditProfile.subredditProfileRecyclerView

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.example.tex.databinding.ItemUserCommentBinding

class SubredditCommentsRVAdapter :
    PagingDataAdapter<SubredditComment, SubredditCommentsRVHolder.CommentsViewHolder>(
        CommentDiffUtil) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): SubredditCommentsRVHolder.CommentsViewHolder {
        return SubredditCommentsRVHolder.CommentsViewHolder(
            ItemUserCommentBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(
        holder: SubredditCommentsRVHolder.CommentsViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position)!!)
    }

    object CommentDiffUtil : DiffUtil.ItemCallback<SubredditComment>() {
        override fun areItemsTheSame(
            oldItem: SubredditComment,
            newItem: SubredditComment,
        ): Boolean {
            return newItem == oldItem
        }

        override fun areContentsTheSame(
            oldItem: SubredditComment,
            newItem: SubredditComment,
        ): Boolean {
            return newItem == oldItem
        }
    }
}