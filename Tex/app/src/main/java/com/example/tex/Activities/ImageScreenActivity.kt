package com.example.tex.Activities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.tex.R
import com.example.tex.databinding.ViewPagerFsItemBinding
import com.example.tex.others.viewPagerAdapters.ViewPagerFullScreenAdapter
import kotlin.collections.ArrayList

class ImageScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        this.window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.image_screen_activity)

        val imageList = intent.extras?.get("IMAGE_LIST") as ArrayList<*>
        val imageThumbnail = intent.getStringExtra("IMAGE_THUMBNAIL")
        val viewPagerItemBinding = ViewPagerFsItemBinding.inflate(LayoutInflater.from(this))
        val viewPager = findViewById<ViewPager2>(R.id.FsImageViewPager)

        val adapter = ViewPagerFullScreenAdapter(imageList.toList() as List<Pair<String, String>>,
            viewPagerItemBinding,
            this,
            imageThumbnail)

        viewPager.adapter = adapter
    }
}