package com.chernishenko.giphytestapp.model

data class GiphyPagedResponse(
    val data: List<GifItem>,
    val nextPage: Int,
    val total: Int,
)
