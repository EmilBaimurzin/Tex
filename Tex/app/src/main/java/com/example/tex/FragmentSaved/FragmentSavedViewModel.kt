package com.example.tex.FragmentSaved

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

class FragmentSavedViewModel(state: SavedStateHandle) : ViewModel() {
    private val savedStateHandle = state

    fun setState(tabPosition: Int) {
        savedStateHandle.set(KEY, tabPosition)
    }

    fun getState(): Int {
        return savedStateHandle.get(KEY) ?: 0
    }

    companion object {
        private val KEY = "state_key"
    }
}