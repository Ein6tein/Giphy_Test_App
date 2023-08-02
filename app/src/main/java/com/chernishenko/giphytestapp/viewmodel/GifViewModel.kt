package com.chernishenko.giphytestapp.viewmodel

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.text2.input.TextFieldState
import androidx.compose.foundation.text2.input.textAsFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.chernishenko.giphytestapp.model.GifItem
import com.chernishenko.giphytestapp.network.GiphyNetworkClient
import com.chernishenko.giphytestapp.paging.GiphyPagingSource
import com.chernishenko.giphytestapp.services.GiphyService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import javax.inject.Inject

@OptIn(ExperimentalFoundationApi::class, FlowPreview::class)
@HiltViewModel
class GifViewModel @Inject constructor() : ViewModel() {

    @Inject lateinit var giphyService: GiphyService

    private lateinit var pagingSource: GiphyPagingSource

    val query = TextFieldState()

    private var lastQuery = ""

    val listData: Flow<PagingData<GifItem>> = Pager(
        PagingConfig(
            pageSize = GiphyNetworkClient.PAGE_SIZE,
            enablePlaceholders = false,
        ),
        pagingSourceFactory = { GiphyPagingSource(query.text.toString(), giphyService).also { pagingSource = it } }
    ).flow.cachedIn(viewModelScope)

    suspend fun run() {
        query.textAsFlow().debounce(500).collectLatest {
            // If text is empty or it is triggered by keyboard close, do nothing
            if (it.toString().isEmpty() || lastQuery == it.toString()) {
                return@collectLatest
            }
            invalidateDataSource()
        }
    }

    fun invalidateDataSource() {
        // As to not double-search same query
        if (lastQuery == query.text.toString()) {
            return
        }
        lastQuery = query.text.toString()
        pagingSource.invalidate()
    }
}
