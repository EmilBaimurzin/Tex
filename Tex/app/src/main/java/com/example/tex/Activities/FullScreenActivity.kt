package com.example.tex.Activities

import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.tex.R
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.PlayerView


class FullScreenActivity : AppCompatActivity() {
    private lateinit var videoView: PlayerView
    private lateinit var fullScreenButton: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        this.window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.full_screen_activity)

        videoView = findViewById(R.id.videoView)
        fullScreenButton = videoView.findViewById(R.id.bt_fullscreen)

        fullScreenButton.setOnClickListener {
            this.finish()
        }

        val url = intent.getStringExtra("videoUrl")!!

        val mediaItem = MediaItem.Builder()
            .setUri(url)
            .build()

        videoView.player = ExoPlayer.Builder(this).build()
        videoView.player?.setMediaItem(mediaItem)
        videoView.player?.prepare()

        if(savedInstanceState != null) {
            savedInstanceState.getInt(MEDIA_ITEM).let { restoredMediaItem ->
                val seekTime = savedInstanceState.getLong(SEEK_TIME)
                videoView.player!!.seekTo(restoredMediaItem, seekTime)
                videoView.player!!.play()
            }
        } else {
            intent.getIntExtra(MEDIA_ITEM, 0).let { restoredMediaItem ->
                val seekTime = intent.getLongExtra(SEEK_TIME, 0)
                videoView.player!!.seekTo(restoredMediaItem, seekTime)
                videoView.player!!.play()
            }
        }
        videoView.player?.play()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putLong(SEEK_TIME, videoView.player!!.currentPosition)
        outState.putInt(MEDIA_ITEM, videoView.player!!.currentMediaItemIndex)
    }

    override fun onStop() {
        super.onStop()
        videoView.player!!.release()
    }

    override fun onDestroy() {
        super.onDestroy()
        videoView.player!!.stop()
    }

    companion object {
        private const val SEEK_TIME = "SEEK_TIME"
        private const val MEDIA_ITEM = "MEDIA_ITEM"
    }
}