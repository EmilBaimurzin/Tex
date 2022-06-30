package com.example.tex.detailedPost.replyDialog

import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CommentReplyViewModel(handle: SavedStateHandle) : ViewModel() {
    private val repository = CommentReplyRepository()
    private val stateHandle = handle
    private val commentLiveData = MutableLiveData<Boolean>()
    val comment: LiveData<Boolean> = commentLiveData

    private val coroutineExceptionHandler =
        CoroutineExceptionHandler { _, t ->
            Log.e("error", "$t")
            commentLiveData.postValue(false)
        }
    private val scope = CoroutineScope(Dispatchers.IO + coroutineExceptionHandler)

    fun sendComment(id: String, text: String) {
        scope.launch {
            commentLiveData.postValue(repository.sendComment(text, id))
        }
    }

    fun setSendAction(value: Boolean) {
        stateHandle.set<Boolean>("SEND_KEY", value)
    }

    fun getSendAction() = stateHandle.get<Boolean>("SEND_KEY") ?: false
}