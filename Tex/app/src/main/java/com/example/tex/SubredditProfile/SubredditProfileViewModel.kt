package com.example.tex.SubredditProfile

import android.util.Log
import androidx.lifecycle.*
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.tex.SubredditProfile.subredditProfileRecyclerView.SubredditComment
import com.example.tex.SubredditProfile.subredditProfileRecyclerView.SubredditCommentsPaging
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class SubredditProfileViewModel(state: SavedStateHandle) : ViewModel() {
    private val savedStateHandle = state
    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, t ->
        Log.e("error", "$t")
    }
    private val repository = SubredditProfileRepository()
    private val subredditLiveData = MutableLiveData<SubredditInfo>()
    private val friendLiveData = MutableLiveData<Int>()
    private val unFriendLiveData = MutableLiveData<Int>()
    private val followLiveData = MutableLiveData<Int>()
    private val unFollowLiveData = MutableLiveData<Int>()

    val scope = CoroutineScope(Dispatchers.IO + coroutineExceptionHandler)

    val subreddit: LiveData<SubredditInfo>
        get() = subredditLiveData

    val friend: LiveData<Int>
        get() = friendLiveData

    val unFriend: LiveData<Int>
        get() = unFriendLiveData

    val follow: LiveData<Int>
        get() = followLiveData

    val unFollow: LiveData<Int>
        get() = unFollowLiveData

    val flowComments: Flow<PagingData<SubredditComment>> =
        Pager(config = PagingConfig(pageSize = 20, prefetchDistance = 2),
            pagingSourceFactory = { SubredditCommentsPaging(getNickNameState()) }
        ).flow.cachedIn(viewModelScope)

    fun getSubredditsInfo(userName: String) {
        scope.launch {
            subredditLiveData.postValue(repository.getSubredditsInfo(userName))
        }
    }

    fun addToFriends(name: String) {
        scope.launch {
            friendLiveData.postValue(repository.addToFriends(name))
        }
    }

    fun removeFromFriends(name: String) {
        scope.launch {
            unFriendLiveData.postValue(repository.removeFromFriends(name))
        }
    }

    fun follow(name: String) {
        scope.launch {
            followLiveData.postValue(repository.follow(name))
        }
    }

    fun unFollow(name: String) {
        scope.launch {
            unFollowLiveData.postValue(repository.unfollow(name))
        }
    }

    fun setFollowState(observeState: Boolean?) {
        savedStateHandle.set(FOLLOW_KEY, observeState)
    }

    fun getFollowState(): Boolean? = savedStateHandle.get(FOLLOW_KEY)

    fun setFriendState(observeState: Boolean?) {
        savedStateHandle.set(FRIEND_KEY, observeState)
    }

    fun getFriendState(): Boolean? = savedStateHandle.get(FRIEND_KEY)

    fun setNickNameState(observeState: String?) {
        savedStateHandle.set(NICKNAME_KEY, observeState)
    }

    private fun getNickNameState(): String = savedStateHandle.get(NICKNAME_KEY)!!

    fun setActionState(value: Boolean?) {
        savedStateHandle.set(ACTION_KEY, value)
    }

    fun getActionState(): Boolean = savedStateHandle.get(ACTION_KEY) ?: false

    companion object {
        private const val FOLLOW_KEY = "FOLLOW_KEY"
        private const val FRIEND_KEY = "FRIEND_KEY"
        private const val NICKNAME_KEY = "NICKNAME_KEY"
        private const val ACTION_KEY = "ACTION_KEY"
    }
}