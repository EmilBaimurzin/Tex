package com.example.tex.main_fragment

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = MainRepository(application)
    private val coroutineExceptionHandler =
        CoroutineExceptionHandler { _, t -> Log.e("error", "$t") }
    private val isConnectedLiveData = MutableLiveData<Boolean>()
    private val isAuthorizedLiveData = MutableLiveData<Pair<Boolean, Boolean>>()
    private val scope = CoroutineScope(Dispatchers.IO + coroutineExceptionHandler)

    val isAuthorized: LiveData<Pair<Boolean, Boolean>>
        get() = isAuthorizedLiveData

    val isConnected: LiveData<Boolean>
        get() = isConnectedLiveData

    fun isConnectedViaWifi() {
        scope.launch {
            isConnectedLiveData.postValue(repository.isConnectedViaWifi())
        }
    }

    fun isAuthorized(action: Boolean) {
        scope.launch {
            isAuthorizedLiveData.postValue(repository.isAuthorized(action))
        }
    }

    fun toSharedPrefs() {
        viewModelScope.launch {
            repository.toSharedPrefs()
        }
    }
}