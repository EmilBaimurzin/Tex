package com.example.tex.FragmentSaved.comments

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.tex.detailedPost.detailedPostRecyclerView.DetailedPostComment
import kotlinx.coroutines.flow.Flow

class SavedCommentsViewModel(state: SavedStateHandle) : ViewModel() {
    private val savedStateHandle = state
    val flowSavedComments: Flow<PagingData<DetailedPostComment>> =
        Pager(config = PagingConfig(pageSize = 20, prefetchDistance = 2),
            pagingSourceFactory = { SavedCommentsPagination() }
        ).flow.cachedIn(viewModelScope)

    val flowMyComments: Flow<PagingData<DetailedPostComment>> =
        Pager(config = PagingConfig(pageSize = 20, prefetchDistance = 2),
            pagingSourceFactory = { MyCommentsPagination() }
        ).flow.cachedIn(viewModelScope)

    fun setIconState(state: Boolean) {
        savedStateHandle.set(ICON_KEY, state)
    }

    fun getIconState(): Boolean {
        return savedStateHandle.get(ICON_KEY) ?: true
    }

    companion object {
        private val ICON_KEY = "ICON_KEY"
    }
}