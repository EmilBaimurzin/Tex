package com.example.tex.FragmentSaved.comments

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.tex.detailedPost.detailedPostRecyclerView.DetailedPostComment

class MyCommentsPagination : PagingSource<String, DetailedPostComment>() {
    override fun getRefreshKey(state: PagingState<String, DetailedPostComment>): String? {
        return null
    }

    override suspend fun load(params: LoadParams<String>): LoadResult<String, DetailedPostComment> {
        val repository = SavedCommentsRepository()

        return try {
            val comments = repository.getMyComments(params.key ?: "null")
            comments.forEach { comment ->
                comment.elementVisibility = false
            }
            return LoadResult.Page(
                data = comments,
                prevKey = null,
                nextKey = comments.last().commentId
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}