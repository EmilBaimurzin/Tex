package com.example.tex.others

import android.content.Context
import android.content.Intent
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

private val scope = CoroutineScope(Dispatchers.Default)

suspend fun millisecondsToTime(milliseconds: String): String {
    return suspendCoroutine { continuation ->
        scope.launch {
            val trueMillis = TimeUnit.SECONDS.toMillis(milliseconds.toDouble().toLong())
            val currentTime = Calendar.getInstance(TimeZone.getTimeZone("UTC")).timeInMillis
            val timePostedMills = currentTime - (trueMillis - 10800000)
            when {
                timePostedMills < 60000 -> continuation.resume(PermanentStorage.NoContextStrings.nowString)
                timePostedMills in 60000..3599999 -> continuation.resume("${
                    TimeUnit.MILLISECONDS.toMinutes(timePostedMills)
                } ${PermanentStorage.NoContextStrings.minutesAgoString}")
                timePostedMills in 3600000..85399999 -> continuation.resume("${
                    TimeUnit.MILLISECONDS.toHours(timePostedMills)
                } ${PermanentStorage.NoContextStrings.hoursAgoString}")
                else -> continuation.resume(
                    "${PermanentStorage.NoContextStrings.postedString} ${Date(trueMillis).date}/${
                        Date(trueMillis).month
                    }/${Date(trueMillis).year - 100}"
                )
            }
        }
    }
}

fun shareWithOthers(link: String, context: Context) {
    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, "https://www.reddit.com$link")
        type = "text/plain"
    }

    val shareIntent = Intent.createChooser(sendIntent, null)
    context.startActivity(shareIntent)
}