package com.example.tex.FragmentSaved.posts

import com.example.tex.others.PermanentStorage
import com.example.tex.others.PostParsing
import com.example.tex.retrofit.Networking
import com.example.tex.subreddit.subredditRecyclerView.RedditPost
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class SavedPostsRepository {
    private val scope = CoroutineScope(Dispatchers.IO)

    suspend fun getSavedPosts(after: String): List<RedditPost> {
        return suspendCoroutine { continuation ->
            scope.launch {
                Networking.mainInterface.getSaved(after = after,
                    limit = "20",
                    type = "links",
                    name = PermanentStorage.name)
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
                                        val posts = PostParsing.getListOfParsedPosts(responseString)
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
}