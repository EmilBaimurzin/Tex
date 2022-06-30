package com.example.tex.auth

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AuthorizationViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = AuthorizationRepository(application)
    private val coroutineExceptionHandler =
        CoroutineExceptionHandler { _, throwable ->
            Log.d("error", "$throwable")
        }
    private val scope = CoroutineScope(Dispatchers.IO + coroutineExceptionHandler)

    fun toSharedPrefs() {
        scope.launch {
            repository.toSharedPrefs()
        }
    }
}