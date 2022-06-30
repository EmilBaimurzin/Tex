package com.example.tex.detailedPost.detailedPostRecyclerView

import android.graphics.Color
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.tex.databinding.ItemCommentBinding

sealed class CommentHomeViewHolder(binding: ViewBinding) : RecyclerView.ViewHolder(binding.root) {
    var itemClickListener: ((item: DetailedPostComment, position: Int, vote: Int, type: CommentClickListenerType) -> Unit)? =
        null

    class CommentViewHolder(private val binding: ItemCommentBinding) :
        CommentHomeViewHolder(binding) {
        fun bind(item: DetailedPostComment) {
            binding.apply {
                nicknameTextView.text = item.nickname
                timeTextView.text = item.time
                descriptionTextView.text = item.description
                upsTextView.text = item.ups.toString()

                if(!item.elementVisibility) {
                    upButton.visibility = View.GONE
                    downButton.visibility = View.GONE
                    saveButton.visibility = View.GONE
                    upsTextView.visibility = View.GONE
                }

                when (item.likes) {
                    true -> {
                        upButton.setColorFilter(Color.rgb(25, 118, 210))
                        downButton.setColorFilter(Color.rgb(189, 189, 189))
                    }
                    false -> {
                        downButton.setColorFilter(Color.rgb(25, 118, 210))
                        upButton.setColorFilter(Color.rgb(189, 189, 189))
                    }
                    null -> {
                        upButton.setColorFilter(Color.rgb(189, 189, 189))
                        downButton.setColorFilter(Color.rgb(189, 189, 189))
                    }
                }

                if (item.isSaved) {
                    saveButton.setColorFilter(Color.rgb(25, 118, 210))
                } else {
                    saveButton.setColorFilter(Color.rgb(189, 189, 189))
                }

                upButton.setOnClickListener {
                    itemClickListener?.invoke(item,
                        bindingAdapterPosition,
                        1,
                        CommentClickListenerType.VOTE)
                }

                downButton.setOnClickListener {
                    itemClickListener?.invoke(item,
                        bindingAdapterPosition,
                        -1,
                        CommentClickListenerType.VOTE)
                }

                saveButton.setOnClickListener {
                    if (!item.isSaved) {
                        itemClickListener?.invoke(item,
                            bindingAdapterPosition,
                            0,
                            CommentClickListenerType.SAVE)
                    } else {
                        itemClickListener?.invoke(item,
                            bindingAdapterPosition,
                            0,
                            CommentClickListenerType.UNSAVE)
                    }
                }

                root.setOnClickListener {
                    itemClickListener?.invoke(item,
                        bindingAdapterPosition,
                        0,
                        CommentClickListenerType.REPLY)
                }

                profileButton.setOnClickListener {
                    itemClickListener?.invoke(item,
                        bindingAdapterPosition,
                        0,
                        CommentClickListenerType.PROFILE)
                }
            }
        }
    }
}