package com.example.tex.detailedPost.detailedPostRecyclerView

import com.example.tex.others.millisecondsToTime
import com.example.tex.retrofit.Networking
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class DetailedPostRepository {
    private val scope = CoroutineScope(Dispatchers.IO)
    suspend fun getComments(link: String, id: String): List<DetailedPostComment> {
        return suspendCoroutine { continuation ->
            scope.launch {
                Networking.mainInterface.getComments(link, id)
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
                                        val comments = getListOfParsedComments(responseString)
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

    private suspend fun getListOfParsedComments(response: String): List<DetailedPostComment> {
        val responseArray = JSONArray(response)
        val json = responseArray.getJSONObject(1)
        val data = json.getJSONObject("data")
        val jsonArray = data.getJSONArray("children")
        val listToReturn = mutableListOf<DetailedPostComment>()

        (0 until jsonArray.length()).map { index -> jsonArray.getJSONObject(index) }
            .map { childJsonObject ->
                val childData = childJsonObject.getJSONObject("data")
                val kind = childJsonObject.getString("kind")
                if (kind != "more") {
                    val name = childData.getString("author")
                    val description = childData.getString("body")
                    val time = millisecondsToTime(childData.getString("created_utc"))
                    val commentId = childData.getString("name")
                    val ups = childData.getInt("ups")
                    val likes = try {
                        childData.getBoolean("likes")
                    } catch (t: Throwable) {
                        null
                    }
                    val isSaved = childData.getBoolean("saved")
                    listToReturn.add(DetailedPostComment(
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
                    ))
                } else {
                    val parentId = childData.getString("parent_id")
                    val moreCommentIdsArray = childData.getJSONArray("children")
                    val listOfIds = mutableListOf<String>()
                    (0 until moreCommentIdsArray.length()).map { index ->
                        moreCommentIdsArray.getString(index)
                    }.map { id ->
                        listOfIds.add(id)
                    }

                    listToReturn.last().apply {
                        this.parentId = parentId
                        this.moreCommentsIds = listOfIds
                    }
                }
            }
        return listToReturn
    }

    suspend fun getMoreComments(linkId: String, listOfIds: String): List<DetailedPostComment> {
        return suspendCoroutine { continuation ->
            scope.launch {
                Networking.mainInterface.getMoreComments("json", linkId, true, listOfIds)
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
                                        val comments = parseMoreComments(responseString)
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

    private suspend fun parseMoreComments(response: String): List<DetailedPostComment> {
        val jsonObject = JSONObject(response)
        val jsonData = jsonObject.getJSONObject("json")
        val data = jsonData.getJSONObject("data")
        val commentsArray = data.getJSONArray("things")
        val listToReturn = mutableListOf<DetailedPostComment>()
        (0 until commentsArray.length()).map { index -> commentsArray.getJSONObject(index) }
            .map { childJsonObject ->
                try {
                    val commentData = childJsonObject.getJSONObject("data")
                    val nickname = commentData.getString("author")
                    val time = millisecondsToTime(commentData.getString("created_utc"))
                    val description = commentData.getString("body")
                    val commentId = commentData.getString("name")
                    val ups = commentData.getInt("ups")
                    val isSaved = commentData.getBoolean("saved")
                    val likes = try {
                        commentData.getBoolean("likes")
                    } catch (t: Throwable) {
                        null
                    }
                    listToReturn.add(DetailedPostComment(nickname = nickname,
                        time = time,
                        description = description,
                        commentId = commentId,
                        moreCommentsIds = emptyList(),
                        parentId = null,
                        ups = ups,
                        likes = likes,
                        isSaved = isSaved,
                        elementVisibility = true
                    ))
                } catch (t: Throwable) {}
            }
        return listToReturn
    }

    fun vote(id: String, vote: Int) {
        scope.launch {
            Networking.mainInterface.vote(id, vote).execute().code()
        }
    }

    suspend fun saveComment(pairIdPosition: Pair<String, Int>): Pair<Boolean, Int> {
        return suspendCoroutine { continuation ->
            scope.launch {
                val code =
                    Networking.mainInterface.saveComment(pairIdPosition.first, "comment").execute()
                        .code()
                continuation.resume(Pair(code == 200, pairIdPosition.second))
            }
        }
    }

    suspend fun unSaveComment(pairIdPosition: Pair<String, Int>): Pair<Boolean, Int> {
        return suspendCoroutine { continuation ->
            scope.launch {
                val code =
                    Networking.mainInterface.unSaveComment(pairIdPosition.first).execute().code()
                continuation.resume(Pair(code == 200, pairIdPosition.second))
            }
        }
    }

    suspend fun sendComment(text: String, postId: String): Boolean {
        return suspendCoroutine { continuation ->
            scope.launch {
                withContext(Dispatchers.IO) {
                    val code =
                        Networking.mainInterface.addComment("json", postId, text).execute().code()
                    continuation.resume(code == 200)
                }
            }
        }
    }

    suspend fun unfollow(name: String): Boolean {
        val code = Networking.mainInterface.unFollow(name).code()
        return code == 200
    }

    suspend fun follow(name: String): Boolean {
        val code = Networking.mainInterface.follow(name).code()
        return code == 200
    }

}
