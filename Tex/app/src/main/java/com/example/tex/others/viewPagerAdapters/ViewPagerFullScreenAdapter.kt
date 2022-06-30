package com.example.tex.others.viewPagerAdapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.example.tex.R
import com.example.tex.databinding.ViewPagerFsItemBinding

class ViewPagerFullScreenAdapter(
    private val images: List<Pair<String, String>>,
    val binding: ViewPagerFsItemBinding,
    val context: Context,
    private val thumbnail: String?,
) : RecyclerView.Adapter<ViewPagerFullScreenAdapter.ViewPagerFSViewHolder>() {
    inner class ViewPagerFSViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerFSViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.view_pager_fs_item, parent, false)
        view.layoutParams = ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT)

        return ViewPagerFSViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewPagerFSViewHolder, position: Int) {
        val currentImagePair = images[position]
        val circularProgressDrawable = CircularProgressDrawable(context)
        circularProgressDrawable.strokeWidth = 5f
        circularProgressDrawable.centerRadius = 30f
        circularProgressDrawable.start()
        val singleThumbnail = Glide.with(context).load(thumbnail)
        val multiThumbnail = Glide.with(context).load(currentImagePair.first)

        if (images.size > 1) {
            Glide.with(context)
                .load(currentImagePair.second)
                .placeholder(circularProgressDrawable)
                .thumbnail(multiThumbnail)
                .into(holder.itemView.findViewById(R.id.fullScreenImageView))
        } else {
            Glide.with(context)
                .load(currentImagePair.second)
                .placeholder(circularProgressDrawable)
                .thumbnail(singleThumbnail)
                .into(holder.itemView.findViewById(R.id.fullScreenImageView))
        }
    }

    override fun getItemCount(): Int {
        return images.size
    }
}