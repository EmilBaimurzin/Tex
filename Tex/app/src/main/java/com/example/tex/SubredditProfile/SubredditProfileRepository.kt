package com.example.tex.SubredditProfile

import android.util.Log
import com.example.tex.SubredditProfile.subredditProfileRecyclerView.SubredditComment
import com.example.tex.retrofit.Networking
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


class SubredditProfileRepository {
    private val scope = CoroutineScope(Dispatchers.IO)

    suspend fun getUsersComments(userName: String, after: String): List<SubredditComment> {
        return suspendCoroutine { continuation ->
            scope.launch {
                Networking.mainInterface.getUsersComments(userName, after = after, limit = 20)
                    .enqueue(object : Callback<ResponseBody> {
                        override fun onResponse(
                            call: Call<ResponseBody>,
                            response: Response<ResponseBody>,
                        ) {
                            if (response.isSuccessful) {
                                scope.launch {
                                    val responseString = response.body()?.string().orEmpty()
                                    continuation.resume(parseCommentsResponse(responseString))
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
    }

    private suspend fun parseCommentsResponse(responseString: String): List<SubredditComment> {
        return suspendCoroutine { continuation ->
            scope.launch {
                withContext(Dispatchers.Default) {
                    val json = JSONObject(responseString)
                    val data = json.getJSONObject("data")
                    val listToReturn = mutableListOf<SubredditComment>()
                    try {
                        val comments = data.getJSONArray("children")
                        (0 until comments.length()).map { index -> comments.getJSONObject(index) }
                            .map { childJsonObject ->
                                val commentData = childJsonObject.getJSONObject("data")
                                val id = commentData.getString("name")
                                val postTitle = commentData.getString("link_title")
                                val community = commentData.getString("subreddit")
                                val selfText = commentData.getString("body")
                                listToReturn.add(SubredditComment(postTitle,
                                    community,
                                    selfText,
                                    id = id))
                            }
                        continuation.resume(listToReturn)
                    } catch (t: Throwable) {
                        Log.e("empty" ,"some", t)
                        continuation.resume(emptyList())
                    }
                }
            }
        }
    }

    suspend fun getSubredditsInfo(userName: String): SubredditInfo? {
        return suspendCoroutine { continuation ->
            scope.launch {
                Networking.mainInterface.getSubredditsInfo(userName)
                    .enqueue(object : Callback<ResponseBody> {
                        override fun onResponse(
                            call: Call<ResponseBody>,
                            response: Response<ResponseBody>,
                        ) {
                            if (response.isSuccessful) {
                                scope.launch {
                                    val responseString = response.body()?.string().orEmpty()
                                    continuation.resume(parseUserInfoResponse(responseString))
                                }
                            } else {
                                continuation.resume(null)
                            }
                        }

                        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                            continuation.resumeWithException(t)
                        }
                    })
            }
        }
    }

    private suspend fun parseUserInfoResponse(responseString: String): SubredditInfo? {
        return suspendCoroutine { continuation ->
            scope.launch {
                withContext(Dispatchers.Default) {
                    val json = JSONObject(responseString)
                    val data = json.getJSONObject("data")
                    try {
                        val isFriend = data.getBoolean("is_friend")
                        val avatar = data.getString("snoovatar_img")
                        val name = data.getString("name")
                        val karma = data.getLong("total_karma")
                        val isFollowed =
                            data.getJSONObject("subreddit").getBoolean("user_is_subscriber")
                        continuation.resume(
                            SubredditInfo(
                                name = name,
                                avatar = avatar,
                                karma = karma,
                                isFriend = isFriend,
                                isFollowed = isFollowed
                            ))
                    } catch (t: Throwable) {
                        continuation.resume(null)
                    }
                }
            }
        }
    }

    fun addToFriends(name: String): Int {
        return Networking.mainInterface.makeUserFriend(name,
            createJsonRequestBody("name" to name, "notes" to "null")).execute().code()
    }

    fun removeFromFriends(name: String): Int {
        return Networking.mainInterface.unfriendUser(name,
            createJsonRequestBody("name" to name, "notes" to "null")).execute().code()
    }

    private fun createJsonRequestBody(vararg params: Pair<String, String>) =
        RequestBody.create(
            "application/json; charset=utf-8".toMediaTypeOrNull(),
            JSONObject(mapOf(*params)).toString())

    suspend fun follow(name: String): Int {
        return Networking.mainInterface.follow("u/$name").code()
    }

    suspend fun unfollow(name: String): Int {
        return Networking.mainInterface.unFollow("u/$name").code()
    }
}