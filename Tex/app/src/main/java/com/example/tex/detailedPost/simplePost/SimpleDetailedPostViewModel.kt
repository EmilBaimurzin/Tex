package com.example.tex.detailedPost.simplePost

import android.util.Log
import androidx.lifecycle.*
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.tex.detailedPost.detailedPostRecyclerView.DetailedPostComment
import com.example.tex.detailedPost.detailedPostRecyclerView.DetailedPostPagination
import com.example.tex.detailedPost.detailedPostRecyclerView.DetailedPostRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class SimpleDetailedPostViewModel(state: SavedStateHandle) : ViewModel() {
    private val stateHandle = state
    private val repository = DetailedPostRepository()
    private val coroutineExceptionHandler =
        CoroutineExceptionHandler { _, t ->
            Log.e("error", "$t")
        }
    private val scope = CoroutineScope(Dispatchers.IO + coroutineExceptionHandler)

    private val saveLiveData = MutableLiveData<Pair<Boolean, Int>>()
    val save: LiveData<Pair<Boolean, Int>> = saveLiveData

    private val commentLiveData = MutableLiveData<Boolean>()
    val comment: LiveData<Boolean> = commentLiveData

    private val followLiveData = MutableLiveData<Boolean>()
    val follow: LiveData<Boolean> = followLiveData

    private val unFollowLiveData = MutableLiveData<Boolean>()
    val unFollow: LiveData<Boolean> = unFollowLiveData

    private val unSaveLiveData = MutableLiveData<Pair<Boolean, Int>>()
    val unSave: LiveData<Pair<Boolean, Int>> = unSaveLiveData

    val flowComments: Flow<PagingData<DetailedPostComment>> =
        Pager(config = PagingConfig(pageSize = 20, prefetchDistance = 2),
            pagingSourceFactory = { DetailedPostPagination(getLink()!!) }
        ).flow.cachedIn(viewModelScope)


    fun setLink(link: String) {
        stateHandle.set<String>("LINK_KEY", link)
    }

    private fun getLink() = stateHandle.get<String>("LINK_KEY")

    fun setSaveAction(value: Boolean) {
        stateHandle.set<Boolean>("SAVE_KEY", value)
    }

    fun getSaveAction() = stateHandle.get<Boolean>("SAVE_KEY") ?: false

    fun setSendAction(value: Boolean) {
        stateHandle.set<Boolean>("SEND_KEY", value)
    }

    fun getSendAction() = stateHandle.get<Boolean>("SEND_KEY") ?: false

    fun setFollowAction(value: Boolean) {
        stateHandle.set<Boolean>("FOLLOW_KEY", value)
    }

    fun getFollowAction() = stateHandle.get<Boolean?>("FOLLOW_KEY")

    fun setFollowObserveAction(value: Boolean) {
        stateHandle.set<Boolean>("OBSERVE", value)
    }

    fun getFollowObserveAction() = stateHandle.get<Boolean>("OBSERVE")

    fun vote(id: String, vote: Int) {
        scope.launch {
            repository.vote(id, vote)
        }
    }

    fun saveComment(id: Pair<String, Int>) {
        scope.launch {
            saveLiveData.postValue(repository.saveComment(id))
        }
    }

    fun unSaveComment(id: Pair<String, Int>) {
        scope.launch {
            unSaveLiveData.postValue(repository.unSaveComment(id))
        }
    }

    fun sendComment(text: String, postId: String) {
        scope.launch {
            commentLiveData.postValue(repository.sendComment(text, postId))
        }
    }

    fun follow(name: String) {
        scope.launch {
            followLiveData.postValue(repository.follow(name))
        }
    }

    fun unfollow(name: String) {
        scope.launch {
            unFollowLiveData.postValue(repository.unfollow(name))
        }
    }
}