package com.example.tex.subreddit

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.liveData
import com.example.tex.DataBase.DataBase
import com.example.tex.DataBase.RedditPostDB
import com.example.tex.others.PermanentStorage
import com.example.tex.others.PostParsing.getListOfParsedPosts
import com.example.tex.retrofit.Networking
import com.example.tex.subreddit.paging.SubredditSearchPostsPaging
import com.example.tex.subreddit.subredditRecyclerView.RedditPost
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

class SubredditRepository {
    private val scope = CoroutineScope(Dispatchers.IO)

    suspend fun getHotPosts(after: String): List<RedditPost> {
        return suspendCoroutine { continuation ->
            scope.launch {
                Networking.mainInterface.getHotPosts(after = after, limit = "20", 1)
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
                                        val posts = getListOfParsedPosts(responseString)
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

    suspend fun getNewPosts(after: String): List<RedditPost> {
        return suspendCoroutine { continuation ->
            scope.launch {
                Networking.mainInterface.getNewPosts(after = after, limit = "20", 1)
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
                                        val posts = getListOfParsedPosts(responseString)
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

    suspend fun searchPosts(after: String, query: String): List<RedditPost> {
        return suspendCoroutine { continuation ->
            scope.launch {
                Networking.mainInterface.searchPosts(after = after,
                    limit = 20,
                    query = query,
                    sr_detail = 1)
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
                                        val posts = getListOfParsedPosts(responseString)
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

    suspend fun getUsersName() {
        scope.launch {
            Networking.mainInterface.getProfileInfo()
                .enqueue(object : Callback<ResponseBody> {
                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        Log.e("error", "error", t)
                    }

                    override fun onResponse(
                        call: Call<ResponseBody>,
                        response: Response<ResponseBody>,
                    ) {
                        scope.launch {
                            withContext(Dispatchers.Default) {
                                if (response.isSuccessful) {
                                    val responseString = response.body()?.string().orEmpty()
                                    val json = JSONObject(responseString)
                                    val name = json.getString("name")
                                    PermanentStorage.name = name
                                }
                            }
                        }
                    }
                })
        }
    }

    suspend fun unfollow(name: String): Int {
        return Networking.mainInterface.unFollow(name).code()
    }

    suspend fun follow(name: String): Int {
        return Networking.mainInterface.follow(name).code()
    }

    suspend fun savePost(item: RedditPost, local: Boolean, online: Boolean): Boolean? {
        return suspendCoroutine { continuation ->
            scope.launch {
                var returnValue: Boolean? = null

                if (local) {
                    val post = RedditPostDB(
                        id = 0,
                        value = item,
                        postId = item.postId
                    )
                    DataBase.instance.dao().addPost(post)
                }

                if (online) {
                    returnValue = savePostOnline(item.postId)
                }

                if (returnValue != null) {
                    continuation.resume(returnValue)
                } else {
                    continuation.resume(null)
                }
            }
        }
    }

    private suspend fun savePostOnline(id: String): Boolean {
        return suspendCoroutine { continuation ->
            val code = Networking.mainInterface.saveById(id, "link").execute().code()
            continuation.resume(code == 200)
        }
    }

    suspend fun unsavePostOnline(item: RedditPost): Boolean {
        return suspendCoroutine { continuation ->
            val code = Networking.mainInterface.unsaveById(item.postId).execute().code()
            continuation.resume(code == 200)
        }
    }

    suspend fun getLocalPosts(pageIndex: Int): List<RedditPost> {
        return suspendCoroutine { continuation ->
            scope.launch {
                withContext(Dispatchers.Default) {
                    val offset = pageIndex * 20
                    val list = DataBase.instance.dao().getLocalPosts(20, offset)
                    val listToReturn = list.map {
                        it.value
                    }
                    continuation.resume(listToReturn)
                }
            }
        }
    }

    suspend fun checkLocal(postId: String): Boolean {
        return suspendCoroutine { continuation ->
            scope.launch {
                withContext(Dispatchers.Default) {
                    val post = DataBase.instance.dao().searchPost(postId)
                    continuation.resume(post != null)
                }
            }
        }
    }

    fun unsaveLocalPost(postId: String) {
        scope.launch(Dispatchers.Default) {
            DataBase.instance.dao().deletePost(postId)
        }
    }

    fun searchPosts(query: String) =
        Pager(config = PagingConfig(pageSize = 20, prefetchDistance = 2),
            pagingSourceFactory = { SubredditSearchPostsPaging(query) }
        ).liveData
}