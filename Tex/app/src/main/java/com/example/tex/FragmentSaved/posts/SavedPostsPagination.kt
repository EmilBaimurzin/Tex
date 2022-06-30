package com.example.tex.FragmentSaved.posts

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.tex.subreddit.subredditRecyclerView.RedditPost

class SavedPostsPagination : PagingSource<String, RedditPost>() {
    override fun getRefreshKey(state: PagingState<String, RedditPost>): String? {
        return null
    }

    override suspend fun load(params: LoadParams<String>): LoadResult<String, RedditPost> {
        val repository = SavedPostsRepository()
        return try {
            val posts = repository.getSavedPosts(params.key ?: "null")
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