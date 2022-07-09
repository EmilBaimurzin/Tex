package com.example.tex.Activities

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.tex.DataBase.DataBase
import com.example.tex.R
import com.example.tex.others.PermanentStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        PermanentStorage.NoContextStrings.nowString = getString(R.string.now)
        PermanentStorage.NoContextStrings.postedString = getString(R.string.posted)
        PermanentStorage.NoContextStrings.minutesAgoString = getString(R.string.minutes_ago)
        PermanentStorage.NoContextStrings.hoursAgoString = getString(R.string.hours_ago)
    }
}