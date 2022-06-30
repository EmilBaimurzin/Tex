package com.example.tex.detailedPost.replyDialog

import com.example.tex.retrofit.Networking
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class CommentReplyRepository {
    private val scope = CoroutineScope(Dispatchers.IO)

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
}