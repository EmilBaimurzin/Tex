package com.example.tex.subreddit.subredditRecyclerView

import android.view.LayoutInflater
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import android.view.ViewGroup
import com.example.tex.R
import com.example.tex.databinding.ImageItemBinding
import com.example.tex.databinding.SimpleItemBinding
import com.example.tex.databinding.VideoItemBinding
import com.example.tex.databinding.ViewpagerItemBinding

class SubredditAdapter : PagingDataAdapter<RedditPost, SubredditRecyclerViewHolder>(NewDiffUtilCallback) {

    var itemClickListener: ((item: RedditPost, position: Int, type: ClickListenerType, name: String) -> Unit)? =
        null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubredditRecyclerViewHolder {
        return when (viewType) {
            R.layout.simple_item -> SubredditRecyclerViewHolder.SimpleViewHolder(
                SimpleItemBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                ), parent.context
            )

            R.layout.image_item -> SubredditRecyclerViewHolder.ImageViewHolder(
                ImageItemBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                ), parent.context,
                ViewpagerItemBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )

            R.layout.video_item -> SubredditRecyclerViewHolder.VideoViewHolder(
                VideoItemBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                ), parent.context
            )

            else -> throw IllegalAccessException("Invalid ViewType Provided")
        }
    }

    override fun onBindViewHolder(holder: SubredditRecyclerViewHolder, position: Int) {
        holder.itemClickListener = itemClickListener
        when (holder) {
            is SubredditRecyclerViewHolder.ImageViewHolder -> holder.bind(getItem(position) as RedditPost.ImagePost)
            is SubredditRecyclerViewHolder.SimpleViewHolder -> holder.bind(getItem(position) as RedditPost.SimplePost)
            is SubredditRecyclerViewHolder.VideoViewHolder -> holder.bind(getItem(position) as RedditPost.VideoPost)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is RedditPost.ImagePost -> R.layout.image_item
            is RedditPost.SimplePost -> R.layout.simple_item
            is RedditPost.VideoPost -> R.layout.video_item
            null -> TODO()
        }
    }

    object NewDiffUtilCallback : DiffUtil.ItemCallback<RedditPost>() {
        override fun areContentsTheSame(oldItem: RedditPost, newItem: RedditPost): Boolean {
            return when {
                oldItem is RedditPost.SimplePost &&
                        newItem is RedditPost.SimplePost -> oldItem.postId == newItem.postId
                oldItem is RedditPost.ImagePost &&
                        newItem is RedditPost.ImagePost -> oldItem.postId == newItem.postId
                oldItem is RedditPost.VideoPost &&
                        newItem is RedditPost.VideoPost -> oldItem.postId == newItem.postId
                else -> false
            }
        }

        override fun areItemsTheSame(oldItem: RedditPost, newItem: RedditPost): Boolean {
            return oldItem == newItem
        }
    }
}
