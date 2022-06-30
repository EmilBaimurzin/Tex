package com.example.tex.subreddit

import androidx.lifecycle.*
import androidx.paging.*
import com.example.tex.subreddit.paging.SubredditHotPostsPaging
import com.example.tex.subreddit.paging.SubredditNewPostsPaging
import com.example.tex.subreddit.subredditRecyclerView.RedditPost
import com.google.android.exoplayer2.util.Log
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class SubredditViewModel(state: SavedStateHandle) : ViewModel() {
    private val savedStateHandle = state
    private val repository = SubredditRepository()
    private val unfollowListLiveData = MutableLiveData<Int>()
    private val followListLiveData = MutableLiveData<Int>()
    private val searchLiveData = MutableLiveData("")
    private val coroutineExceptionHandler =
        CoroutineExceptionHandler { _, throwable ->
            Log.e("error", "$throwable")
        }
    private val scope = CoroutineScope(Dispatchers.IO + coroutineExceptionHandler)
    val searchPosts = searchLiveData.switchMap { query ->
        repository.searchPosts(query).cachedIn(viewModelScope)
    }

    val flowNewPost: Flow<PagingData<RedditPost>> =
        Pager(config = PagingConfig(pageSize = 20, prefetchDistance = 2),
            pagingSourceFactory = { SubredditNewPostsPaging() }
        ).flow.cachedIn(viewModelScope)

    val flowHotPost: Flow<PagingData<RedditPost>> =
        Pager(config = PagingConfig(pageSize = 20, prefetchDistance = 2),
            pagingSourceFactory = { SubredditHotPostsPaging() }
        ).flow.cachedIn(viewModelScope)

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

    fun getUsersName() {
        scope.launch {
            repository.getUsersName()
        }
    }

    fun searchPosts(query: String) {
        searchLiveData.value = query
    }

    fun setTabState(state: Int) {
        savedStateHandle.set(TAB_KEY, state)
    }

    fun getTabState(): Int {
        return savedStateHandle.get(TAB_KEY) ?: 0
    }

    fun setSubredditNameState(state: String) {
        savedStateHandle.set(SUBREDDIT_NAME_KEY, state)
    }

    fun getSubredditNameState(): String {
        return savedStateHandle.get(SUBREDDIT_NAME_KEY) ?: ""
    }

    fun setSearchState(state: Boolean) {
        savedStateHandle.set(SEARCH_KEY, state)
    }

    fun getSearchState(): Boolean {
        return savedStateHandle.get(SEARCH_KEY) ?: false
    }

    companion object {
        private const val TAB_KEY = "TAB_KEY"
        private const val SUBREDDIT_NAME_KEY = "SUBREDDIT_NAME_KEY"
        private const val SEARCH_KEY = "SEARCH_KEY"
    }
}
