package com.example.tex.others.viewPagerAdapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.tex.R
import com.example.tex.databinding.ViewpagerItemBinding


class ViewPagerAdapter(
    val images: List<Pair<String, String>>,
    val binding: ViewpagerItemBinding,
    val context: Context,
    val thumbnail: String?,
    val callback: () -> Unit
) : RecyclerView.Adapter<ViewPagerAdapter.ViewPagerViewHolder>() {
    inner class ViewPagerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.viewpager_item, parent, false)
        view.layoutParams = ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT)

        return ViewPagerViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewPagerViewHolder, position: Int) {
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
                .apply(RequestOptions().dontTransform().placeholder(circularProgressDrawable))
                .thumbnail(multiThumbnail)
                .into(holder.itemView.findViewById(R.id.ivImage))
        } else {
            Glide.with(context)
                .load(currentImagePair.second)
                .apply(RequestOptions().dontTransform().placeholder(circularProgressDrawable))
                .thumbnail(singleThumbnail)
                .into(holder.itemView.findViewById(R.id.ivImage))
        }

        holder.itemView.setOnClickListener {
            callback.invoke()
        }
    }

    override fun getItemCount(): Int {
        return images.size
    }
}