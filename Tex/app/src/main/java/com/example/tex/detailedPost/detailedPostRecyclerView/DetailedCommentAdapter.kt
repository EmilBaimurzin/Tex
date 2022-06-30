package com.example.tex.detailedPost.detailedPostRecyclerView

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.example.tex.databinding.ItemCommentBinding

class DetailedCommentAdapter :
    PagingDataAdapter<DetailedPostComment, CommentHomeViewHolder.CommentViewHolder>(
        DetailedCommentDiffUtil) {

    var itemClickListener: ((item: DetailedPostComment, position: Int, vote: Int, type: CommentClickListenerType) -> Unit)? =
        null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): CommentHomeViewHolder.CommentViewHolder {
        return CommentHomeViewHolder.CommentViewHolder(
            ItemCommentBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: CommentHomeViewHolder.CommentViewHolder, position: Int) {
        holder.itemClickListener = itemClickListener
        holder.bind(getItem(position)!!)
    }

    object DetailedCommentDiffUtil : DiffUtil.ItemCallback<DetailedPostComment>() {
        override fun areItemsTheSame(
            oldItem: DetailedPostComment,
            newItem: DetailedPostComment,
        ): Boolean {
            return newItem == oldItem
        }

        override fun areContentsTheSame(
            oldItem: DetailedPostComment,
            newItem: DetailedPostComment,
        ): Boolean {
            return newItem == oldItem
        }
    }
}