package com.example.tex.profileFragment

import android.content.Context
import com.example.tex.retrofit.Networking
import com.google.android.exoplayer2.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class ProfileRepository {
    private val scope = CoroutineScope(Dispatchers.IO)
    suspend fun getUserInfo(): UserInfo? {
        return suspendCoroutine { continuation ->
            scope.launch {
                Networking.mainInterface.getProfileInfo().enqueue(object : Callback<ResponseBody> {
                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        continuation.resumeWithException(t)
                    }

                    override fun onResponse(
                        call: Call<ResponseBody>,
                        response: Response<ResponseBody>,
                    ) {
                        if (response.isSuccessful) {
                            val responseString = response.body()?.string().orEmpty()
                            continuation.resume(getUsefulInfo(responseString))
                        } else {
                            continuation.resume(null)
                        }
                    }
                })
            }
        }
    }

    private fun getUsefulInfo(responseString: String): UserInfo {
        val responseJson = JSONObject(responseString)
        val subreddit = responseJson.getJSONObject("subreddit")
        val name = subreddit.getString("display_name")
        val avatar = try {
            responseJson.getString("snoovatar_img")
        } catch (t: Throwable) {
            ""
        }
        val friends = responseJson.getInt("num_friends")
        return UserInfo(name = name, avatar = avatar, friendNumber = friends)
    }
}