package com.example.tex.friendList

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FriendListViewModel : ViewModel() {
    private val repository = FriendListRepository()
    private val coroutineExceptionHandler =
        CoroutineExceptionHandler { _, t ->
            Log.e("error", "$t")
        }
    private val scope = CoroutineScope(Dispatchers.IO + coroutineExceptionHandler)
    private val friendsLiveData = MutableLiveData<List<String>>()

    val friends: LiveData<List<String>>
        get() = friendsLiveData

    fun getFriends() {
        scope.launch {
            friendsLiveData.postValue(repository.getFriends())
        }
    }
}