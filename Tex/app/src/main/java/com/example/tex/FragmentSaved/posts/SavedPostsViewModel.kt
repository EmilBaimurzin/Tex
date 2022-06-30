package com.example.tex.FragmentSaved.posts

import androidx.lifecycle.*
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.tex.subreddit.SubredditRepository
import com.example.tex.subreddit.subredditRecyclerView.RedditPost
import com.google.android.exoplayer2.util.Log
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class SavedPostsViewModel(state: SavedStateHandle) : ViewModel() {
    private val savedStateHandle = state
    val flowSavedPost: Flow<PagingData<RedditPost>> =
        Pager(config = PagingConfig(pageSize = 20, prefetchDistance = 2),
            pagingSourceFactory = { SavedPostsPagination() }
        ).flow.cachedIn(viewModelScope)

    private val unfollowListLiveData = MutableLiveData<Int>()
    private val followListLiveData = MutableLiveData<Int>()
    private val coroutineExceptionHandler =
        CoroutineExceptionHandler { _, throwable ->
            Log.e("error", "$throwable")
        }
    private val scope = CoroutineScope(Dispatchers.IO + coroutineExceptionHandler)
    private val repository = SubredditRepository()

    val unfollow: LiveData<Int>
        get() = unfollowListLiveData

    val follow: LiveData<Int>
        get() = followListLiveData

    fun unfollow(name: String) {
        scope.launch {
            unfollowListLiveData.postValue(repository.unfollow(name))
        }
    }

    fun follow(name: String) {
        scope.launch {
            followListLiveData.postValue(repository.follow(name))
        }
    }

    fun setSubredditNameState(state: String) {
        savedStateHandle.set("SUBREDDIT_NAME_KEY", state)
    }

    fun getSubredditNameState(): String {
        return savedStateHandle.get("SUBREDDIT_NAME_KEY") ?: ""
    }
}