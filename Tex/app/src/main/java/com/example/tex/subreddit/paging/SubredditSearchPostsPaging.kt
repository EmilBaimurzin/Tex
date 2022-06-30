package com.example.tex.subreddit.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.tex.subreddit.SubredditRepository
import com.example.tex.subreddit.subredditRecyclerView.RedditPost

class SubredditSearchPostsPaging(private val query: String) : PagingSource<String, RedditPost>() {
    override fun getRefreshKey(state: PagingState<String, RedditPost>): String? {
        return null
    }

    override suspend fun load(params: LoadParams<String>): LoadResult<String, RedditPost> {
        val repository = SubredditRepository()

        return try {
            val posts = repository.searchPosts(params.key ?: "null", query = query)
            return LoadResult.Page(
                data = posts,
                prevKey = null,
                nextKey = posts.last().postId
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}