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
    private val saveLiveData = MutableLiveData<Boolean?>()
    private val unsaveLiveData = MutableLiveData<Boolean>()
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

    val save: LiveData<Boolean?> = saveLiveData
    val unsave: LiveData<Boolean> = unsaveLiveData

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

    fun savePost(item: RedditPost, local: Boolean, online: Boolean) {
        scope.launch {
            saveLiveData.postValue(repository.savePost(item, local, online))
        }
    }

    fun unsavePost(item: RedditPost) {
        scope.launch {
            unsaveLiveData.postValue(repository.unsavePostOnline(item))
        }
    }

    fun checkLocal(postId: String, callback: (Boolean) -> Unit) {
        scope.launch {
            callback(repository.checkLocal(postId))
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

    fun setActionState(state: Boolean) {
        savedStateHandle.set(ACTION_KEY, state)
    }

    fun getActionState(): Boolean {
        return savedStateHandle.get(ACTION_KEY) ?: false
    }

    fun setPositionState(state: Int) {
        savedStateHandle.set(POSITION_KEY, state)
    }

    fun getPositionState(): Int {
        return savedStateHandle.get(POSITION_KEY) ?: 0
    }

    companion object {
        private const val TAB_KEY = "TAB_KEY"
        private const val SUBREDDIT_NAME_KEY = "SUBREDDIT_NAME_KEY"
        private const val SEARCH_KEY = "SEARCH_KEY"
        private const val ACTION_KEY = "ACTION_KEY"
        private const val POSITION_KEY = "POSITION_KEY"
    }
}
