package com.example.tex.detailedPost.detailedPostRecyclerView

import androidx.paging.PagingSource
import androidx.paging.PagingState

class DetailedPostPagination(
    private val link: String,
) : PagingSource<Pair<List<String>, String>, DetailedPostComment>() {
    override fun getRefreshKey(state: PagingState<Pair<List<String>, String>, DetailedPostComment>): Pair<List<String>, String>? {
        return null
    }

    override suspend fun load(params: LoadParams<Pair<List<String>, String>>): LoadResult<Pair<List<String>, String>, DetailedPostComment> {
        val repository = DetailedPostRepository()

        return try {
            val comments: List<DetailedPostComment>
            val nextKey: Pair<List<String>, String>?

            if (params.key == null) {
                comments = repository.getComments(link, "")
                nextKey = if (comments.last().moreCommentsIds.isNotEmpty()) {
                    Pair(comments.last().moreCommentsIds, comments.last().parentId!!)
                } else {
                    null
                }
            } else {
                if (params.key!!.first.size > 20) {
                    val commentsString = params.key!!.first.take(20).toString()
                        .replace("]", "")
                        .replace("[", "")
                    comments = repository.getMoreComments(params.key!!.second, commentsString)
                    val nextList = params.key!!.first as MutableList
                    nextList.subList(0, 19).clear()
                    nextKey = Pair(nextList, params.key!!.second)
                } else {
                    val commentsString = params.key!!.toString()
                        .replace("]", "")
                        .replace("[", "")
                    comments = repository.getComments(params.key!!.second, commentsString)
                    nextKey = null
                }
            }
            return LoadResult.Page(
                data = comments,
                prevKey = null,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override val keyReuseSupported: Boolean
        get() = true
}