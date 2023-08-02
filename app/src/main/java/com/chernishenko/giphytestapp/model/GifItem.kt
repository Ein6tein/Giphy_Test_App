package com.chernishenko.giphytestapp.model

import com.google.gson.annotations.SerializedName

data class GifItem(
    @SerializedName("url") val url: String,
    @SerializedName("webp") val webp: String?,
)
