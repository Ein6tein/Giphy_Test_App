package com.chernishenko.giphytestapp.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.chernishenko.giphytestapp.model.GifItem
import com.chernishenko.giphytestapp.model.GiphyPagedResponse
import com.chernishenko.giphytestapp.services.GiphyService

class GiphyPagingSource(
    private val query: String,
    private val giphyService: GiphyService,
) : PagingSource<Int, GifItem>() {

    override fun getRefreshKey(state: PagingState<Int, GifItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override val keyReuseSupported: Boolean
        get() = true

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, GifItem> {
        val currentPageNumber = params.key ?: 0

        if (query.isEmpty()) {
            return LoadResult.Page(
                prevKey = null,
                nextKey = null,
                data = emptyList(),
            )
        }

        return try {
            val response: GiphyPagedResponse = giphyService.searchGifs(query, currentPageNumber)

            val nextKey = response.nextPage

            LoadResult.Page(
                prevKey = null,
                nextKey = nextKey,
                data = response.data,
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}
