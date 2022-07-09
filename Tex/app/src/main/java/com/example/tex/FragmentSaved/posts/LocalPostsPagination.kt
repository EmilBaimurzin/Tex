package com.example.tex.FragmentSaved.posts

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.tex.subreddit.SubredditRepository
import com.example.tex.subreddit.subredditRecyclerView.RedditPost

class LocalPostsPagination : PagingSource<Int, RedditPost>() {
    override fun getRefreshKey(state: PagingState<Int, RedditPost>): Int? {
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, RedditPost> {
        val pageIndex = params.key ?: 0
        val repository = SubredditRepository()
        return try {
            val posts = repository.getLocalPosts(pageIndex)
                posts.map {
                it.isLocal = true
            }
            return LoadResult.Page(
                data = posts,
                prevKey = null,
                nextKey = if(posts.size == params.loadSize) pageIndex + 1 else null
            )
        } catch (t: Throwable) {
            LoadResult.Error(t)
        }
    }
}