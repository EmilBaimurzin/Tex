package com.example.tex.SubredditProfile.subredditProfileRecyclerView

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.tex.SubredditProfile.SubredditProfileRepository

class SubredditCommentsPaging(
    private val username: String
): PagingSource<String, SubredditComment>() {
    override fun getRefreshKey(state: PagingState<String, SubredditComment>): String? {
        return null
    }

    override suspend fun load(params: LoadParams<String>): LoadResult<String, SubredditComment> {
        val repository = SubredditProfileRepository()

        return try {
            val comments = repository.getUsersComments(username, params.key ?: "null")
            return LoadResult.Page(
                data = comments,
                prevKey = null,
                nextKey = comments.last().id
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}