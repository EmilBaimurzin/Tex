package com.example.tex.subreddit.subredditRecyclerView

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Icon
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.tex.R
import com.example.tex.databinding.ImageItemBinding
import com.example.tex.databinding.SimpleItemBinding
import com.example.tex.databinding.VideoItemBinding
import com.example.tex.databinding.ViewpagerItemBinding
import com.example.tex.others.viewPagerAdapters.ViewPagerAdapter
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

sealed class SubredditRecyclerViewHolder(binding: ViewBinding) : RecyclerView.ViewHolder(binding.root) {

    var itemClickListener: ((item: RedditPost, position: Int, type: ClickListenerType, name: String) -> Unit)? =
        null

    class SimpleViewHolder(private val binding: SimpleItemBinding, private val context: Context) :
        SubredditRecyclerViewHolder(binding) {
        fun bind(item: RedditPost.SimplePost) {
            if (item.isFollowed) {
                binding.subscribeButton.setImageIcon(Icon.createWithResource(context,
                    R.drawable.ic_baseline_done_24))
            } else {
                binding.subscribeButton.setImageIcon(Icon.createWithResource(context,
                    R.drawable.ic_add))
            }

            if (item.isLocal) {
                binding.timeTextView.visibility = View.INVISIBLE
                binding.saveButton.visibility = View.GONE
                binding.subscribeButton.visibility = View.GONE
                binding.commentsTextView.visibility = View.INVISIBLE
            } else {
                binding.timeTextView.visibility = View.VISIBLE
                binding.saveButton.visibility = View.VISIBLE
                binding.subscribeButton.visibility = View.VISIBLE
                binding.commentsTextView.visibility = View.VISIBLE
            }

            if (item.isSaved) {
                binding.saveButton.setColorFilter(Color.rgb(25, 118, 210))
            } else {
                binding.saveButton.setColorFilter(Color.rgb(189, 189, 189))
            }

            binding.apply {
                root.setOnClickListener {
                    itemClickListener?.invoke(item,
                        bindingAdapterPosition,
                        ClickListenerType.NAVIGATE,
                        item.nickName)
                }

                shareButton.setOnClickListener {
                    itemClickListener?.invoke(item,
                        bindingAdapterPosition,
                        ClickListenerType.SHARE,
                        item.nickName)
                }

                subscribeButton.setOnClickListener {
                    itemClickListener?.invoke(item,
                        bindingAdapterPosition,
                        isFollowed(item),
                        item.nickName)
                }

                profileButton.setOnClickListener {
                    itemClickListener?.invoke(item,
                        bindingAdapterPosition,
                        ClickListenerType.PROFILE,
                        item.nickName)
                }

                subscribeButton.setOnClickListener {
                    itemClickListener?.invoke(item,
                        bindingAdapterPosition,
                        isFollowed(item),
                        item.nickName)
                }

                saveButton.setOnClickListener {
                    if (!item.isSaved) {
                        itemClickListener?.invoke(item,
                            bindingAdapterPosition,
                            ClickListenerType.SAVE,
                            item.nickName)
                    } else {
                        itemClickListener?.invoke(item,
                            bindingAdapterPosition,
                            ClickListenerType.UNSAVE,
                            item.nickName)
                    }
                }

                Glide.with(itemView)
                    .load(item.avatar)
                    .apply(RequestOptions.circleCropTransform())
                    .placeholder(R.drawable.placeholder)
                    .into(avatarImageView)

                commentsTextView.text = item.comments
                descriptionTextView.text = item.description
                nicknameTextView.text = item.nickName
                titleTextView.text = item.title
                timeTextView.text = item.time
            }
        }
    }

    class ImageViewHolder(
        private val binding: ImageItemBinding,
        private val context: Context,
        private val viewPagerItemBinding: ViewpagerItemBinding,
    ) :
        SubredditRecyclerViewHolder(binding) {
        fun bind(item: RedditPost.ImagePost) {
            if (item.isFollowed) {
                binding.subscribeButton.setImageIcon(Icon.createWithResource(context,
                    R.drawable.ic_baseline_done_24))
            } else {
                binding.subscribeButton.setImageIcon(Icon.createWithResource(context,
                    R.drawable.ic_add))
            }

            if (item.isLocal) {
                binding.timeTextView.visibility = View.INVISIBLE
                binding.saveButton.visibility = View.GONE
                binding.subscribeButton.visibility = View.GONE
                binding.commentsTextView.visibility = View.INVISIBLE
            } else {
                binding.timeTextView.visibility = View.VISIBLE
                binding.saveButton.visibility = View.VISIBLE
                binding.subscribeButton.visibility = View.VISIBLE
                binding.commentsTextView.visibility = View.VISIBLE
            }

            if (item.isSaved) {
                binding.saveButton.setColorFilter(Color.rgb(25, 118, 210))
            } else {
                binding.saveButton.setColorFilter(Color.rgb(189, 189, 189))
            }

            binding.apply {
                root.setOnClickListener {
                    itemClickListener?.invoke(item,
                        bindingAdapterPosition,
                        ClickListenerType.NAVIGATE,
                        item.nickName)
                }

                shareButton.setOnClickListener {
                    itemClickListener?.invoke(item,
                        bindingAdapterPosition,
                        ClickListenerType.SHARE,
                        item.nickName)
                }

                subscribeButton.setOnClickListener {
                    itemClickListener?.invoke(item,
                        bindingAdapterPosition,
                        isFollowed(item),
                        item.nickName)
                }

                saveButton.setOnClickListener {
                    if (!item.isSaved) {
                        itemClickListener?.invoke(item,
                            bindingAdapterPosition,
                            ClickListenerType.SAVE,
                            item.nickName)
                    } else {
                        itemClickListener?.invoke(item,
                            bindingAdapterPosition,
                            ClickListenerType.UNSAVE,
                            item.nickName)
                    }
                }

                Glide.with(itemView)
                    .load(item.avatar)
                    .apply(RequestOptions.circleCropTransform())
                    .placeholder(R.drawable.placeholder)
                    .into(avatarImageView)

                commentsTextView.text = item.comments
                nicknameTextView.text = item.nickName
                titleTextView.text = item.title
                timeTextView.text = item.time

                CoroutineScope(Dispatchers.Main).launch {
                    if (item.media.size > 1) {
                        imageInfoTextView.text = item.media.size.toString()
                        image.visibility = View.VISIBLE
                    } else {
                        imageInfoTextView.text = ""
                        image.visibility = View.GONE
                    }
                }
            }
            val adapter =
                ViewPagerAdapter(item.media, viewPagerItemBinding, context, item.thumbnail) {}
            binding.imageViewPager.adapter = adapter
        }
    }

    class VideoViewHolder(private val binding: VideoItemBinding, private val context: Context) :
        SubredditRecyclerViewHolder(binding) {
        fun bind(item: RedditPost.VideoPost) {
            if (item.isFollowed) {
                binding.subscribeButton.setImageIcon(Icon.createWithResource(context,
                    R.drawable.ic_baseline_done_24))
            } else {
                binding.subscribeButton.setImageIcon(Icon.createWithResource(context,
                    R.drawable.ic_add))
            }

            if (item.isLocal) {
                binding.timeTextView.visibility = View.INVISIBLE
                binding.saveButton.visibility = View.GONE
                binding.subscribeButton.visibility = View.GONE
                binding.commentsTextView.visibility = View.INVISIBLE
            } else {
                binding.timeTextView.visibility = View.VISIBLE
                binding.saveButton.visibility = View.VISIBLE
                binding.subscribeButton.visibility = View.VISIBLE
                binding.commentsTextView.visibility = View.VISIBLE
            }

            if (item.isSaved) {
                binding.saveButton.setColorFilter(Color.rgb(25, 118, 210))
            } else {
                binding.saveButton.setColorFilter(Color.rgb(189, 189, 189))
            }

            binding.apply {
                root.setOnClickListener {
                    itemClickListener?.invoke(item,
                        bindingAdapterPosition,
                        ClickListenerType.NAVIGATE,
                        item.nickName)
                }

                shareButton.setOnClickListener {
                    itemClickListener?.invoke(item,
                        bindingAdapterPosition,
                        ClickListenerType.SHARE,
                        item.nickName)
                }

                subscribeButton.setOnClickListener {
                    itemClickListener?.invoke(item,
                        bindingAdapterPosition,
                        isFollowed(item),
                        item.nickName)
                }

                saveButton.setOnClickListener {
                    if (!item.isSaved) {
                        itemClickListener?.invoke(item,
                            bindingAdapterPosition,
                            ClickListenerType.SAVE,
                            item.nickName)
                    } else {
                        itemClickListener?.invoke(item,
                            bindingAdapterPosition,
                            ClickListenerType.UNSAVE,
                            item.nickName)
                    }
                }

                Glide.with(itemView)
                    .load(item.avatar)
                    .placeholder(R.drawable.placeholder)
                    .apply(RequestOptions.circleCropTransform())
                    .into(avatarImageView)

                commentsTextView.text = item.comments
                nicknameTextView.text = item.nickName
                titleTextView.text = item.title
                timeTextView.text = item.time
            }

            val mediaItem = MediaItem.Builder()
                .setUri(item.video)
                .build()

            binding.videoView.player = ExoPlayer.Builder(context).build()
            binding.videoView.player?.setMediaItem(mediaItem)
            binding.videoView.findViewById<ImageView>(R.id.bt_fullscreen).visibility = View.GONE
            binding.videoView.player?.prepare()
            binding.videoView.player?.play()
        }
    }

    protected fun isFollowed(item: RedditPost): ClickListenerType {
        return if (item.isFollowed) {
            ClickListenerType.UNFOLLOW
        } else {
            ClickListenerType.FOLLOW
        }
    }
}