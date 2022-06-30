package com.example.tex.profileFragment

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {
    private val repository = ProfileRepository()
    private val userInfoLiveData = MutableLiveData<UserInfo>()
    private val coroutineExceptionHandler =
        CoroutineExceptionHandler { _, t ->
            Log.e("error", "$t")
        }
    private val scope = CoroutineScope(Dispatchers.IO + coroutineExceptionHandler)

    val userInfo: LiveData<UserInfo>
        get() = userInfoLiveData

    fun getUserInfo() {
        scope.launch {
            userInfoLiveData.postValue(repository.getUserInfo())
        }
    }
}