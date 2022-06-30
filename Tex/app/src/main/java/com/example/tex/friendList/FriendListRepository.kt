package com.example.tex.friendList

import com.example.tex.retrofit.Networking
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class FriendListRepository {
    private val scope = CoroutineScope(Dispatchers.IO)

    suspend fun getFriends(): List<String> {
        return suspendCoroutine { continuation ->
            Networking.mainInterface.getFriends().enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>,
                ) {
                    if (response.isSuccessful) {
                        scope.launch {
                            withContext(Dispatchers.Default) {
                                val responseString = response.body()?.string().orEmpty()
                                continuation.resume(parseResponse(responseString))
                            }
                        }
                    } else {
                        continuation.resume(emptyList())
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    continuation.resumeWithException(t)
                }
            })
        }
    }

    private suspend fun parseResponse(response: String): List<String> {
        return suspendCoroutine { continuation ->
            scope.launch {
                withContext(Dispatchers.Default) {
                    val responseJSON = JSONObject(response)
                    val jsonArray = responseJSON.getJSONObject("data").getJSONArray("children")
                    val names = mutableListOf<String>()
                    try {
                        (0 until jsonArray.length()).map { index -> jsonArray.getJSONObject(index) }
                            .map { childJsonObject ->
                                names.add(childJsonObject.getString("name"))
                            }
                        continuation.resume(names)
                    } catch (t: Throwable) {
                        continuation.resume(emptyList())
                    }
                }
            }
        }
    }
}