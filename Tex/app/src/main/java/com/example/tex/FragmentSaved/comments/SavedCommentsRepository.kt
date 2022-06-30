package com.example.tex.FragmentSaved.comments

import com.example.tex.detailedPost.detailedPostRecyclerView.DetailedPostComment
import com.example.tex.others.PermanentStorage
import com.example.tex.others.millisecondsToTime
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

class SavedCommentsRepository {
    private val scope = CoroutineScope(Dispatchers.IO)
    suspend fun getSavedComments(after: String): List<DetailedPostComment> {
        return suspendCoroutine { continuation ->
            scope.launch {
                Networking.mainInterface.getSaved(after = after,
                    limit = "20",
                    name = PermanentStorage.name,
                    type = "comments")
                    .enqueue(object : Callback<ResponseBody> {
                        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                            continuation.resumeWithException(t)
                        }

                        override fun onResponse(
                            call: Call<ResponseBody>,
                            response: Response<ResponseBody>,
                        ) {
                            scope.launch {
                                withContext(Dispatchers.Default) {
                                    if (response.isSuccessful) {
                                        val responseString = response.body()?.string().orEmpty()
                                        val comments = getListOfSavedParsedComments(responseString)
                                        continuation.resume(comments)
                                    } else {
                                        continuation.resume(emptyList())
                                    }
                                }
                            }
                        }
                    })
            }
        }
    }

    suspend fun getMyComments(after: String): List<DetailedPostComment> {
        return suspendCoroutine { continuation ->
            scope.launch {
                Networking.mainInterface.getUsersComments(name = PermanentStorage.name,
                    after = after,
                    limit = 20)
                    .enqueue(object : Callback<ResponseBody> {
                        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                            continuation.resumeWithException(t)
                        }

                        override fun onResponse(
                            call: Call<ResponseBody>,
                            response: Response<ResponseBody>,
                        ) {
                            scope.launch {
                                withContext(Dispatchers.Default) {
                                    if (response.isSuccessful) {
                                        val responseString = response.body()?.string().orEmpty()
                                        val posts = getListOfMyParsedComments(responseString)
                                        continuation.resume(posts)
                                    } else {
                                        continuation.resume(emptyList())
                                    }
                                }
                            }
                        }
                    })
            }
        }
    }

    private suspend fun getListOfSavedParsedComments(response: String): List<DetailedPostComment> {
        return suspendCoroutine { continuation ->
            scope.launch {
                withContext(Dispatchers.Default) {
                    val json = JSONObject(response)
                    val data = json.getJSONObject("data")
                    val jsonArray = data.getJSONArray("children")
                    val listToReturn = mutableListOf<DetailedPostComment>()

                    (0 until jsonArray.length()).map { index -> jsonArray.getJSONObject(index) }
                        .map { childJsonObject ->
                            var comment: DetailedPostComment
                            try {
                                val childData = childJsonObject.getJSONObject("data")
                                val name = childData.getString("author")
                                val description = childData.getString("body")
                                val time = millisecondsToTime(childData.getString("created_utc"))
                                val commentId = childData.getString("name")
                                val ups = childData.getInt("ups")
                                val isSaved = childData.getBoolean("saved")
                                val likes = try {
                                    childData.getBoolean("likes")
                                } catch (t: Throwable) {
                                    null
                                }
                                comment = DetailedPostComment(
                                    nickname = name,
                                    description = description,
                                    time = time,
                                    commentId = commentId,
                                    moreCommentsIds = emptyList(),
                                    parentId = null,
                                    ups = ups,
                                    likes = likes,
                                    isSaved = isSaved,
                                    elementVisibility = true
                                )
                            } catch (t: Throwable) {
                                comment = DetailedPostComment(
                                    nickname = "",
                                    description = "",
                                    time = "",
                                    commentId = "",
                                    moreCommentsIds = emptyList(),
                                    parentId = null,
                                    ups = 0,
                                    likes = null,
                                    isSaved = false,
                                    elementVisibility = true
                                )
                            }
                            if (comment.nickname != "" && comment.description != "") {
                                listToReturn.add(comment)
                            }
                        }
                    continuation.resume(listToReturn)
                }
            }
        }
    }

    private suspend fun getListOfMyParsedComments(responseString: String): List<DetailedPostComment> {
        val json = JSONObject(responseString)
        val data = json.getJSONObject("data")
        val listToReturn = mutableListOf<DetailedPostComment>()
        return suspendCoroutine { continuation ->
            scope.launch {
                withContext(Dispatchers.Default) {
                    try {
                        val comments = data.getJSONArray("children")
                        (0 until comments.length()).map { index -> comments.getJSONObject(index) }
                            .map { childJsonObject ->
                                val commentData = childJsonObject.getJSONObject("data")
                                val time =
                                    millisecondsToTime(commentData.getString("created_utc"))
                                val description = commentData.getString("body")
                                val id = commentData.getString("name")
                                val ups = commentData.getInt("ups")
                                val isSaved = commentData.getBoolean("saved")
                                val likes = try {
                                    commentData.getBoolean("likes")
                                } catch (t: Throwable) {
                                    null
                                }
                                listToReturn.add(DetailedPostComment(
                                    nickname = PermanentStorage.name,
                                    time,
                                    description = description,
                                    id,
                                    moreCommentsIds = emptyList(),
                                    parentId = null,
                                    ups = ups,
                                    likes,
                                    isSaved,
                                    elementVisibility = true
                                ))
                            }

                        continuation.resume(listToReturn)
                    } catch (t: Throwable) {
                        continuation.resume(emptyList())
                    }
                }
            }
        }
    }
}