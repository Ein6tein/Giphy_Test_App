package com.chernishenko.giphytestapp.services

import com.chernishenko.giphytestapp.exceptions.EmptyBodyException
import com.chernishenko.giphytestapp.model.GifItem
import com.chernishenko.giphytestapp.model.GiphyPagedResponse
import com.chernishenko.giphytestapp.network.GiphyNetworkClient
import com.chernishenko.giphytestapp.repositories.GiphyRepository
import com.google.gson.JsonObject
import javax.inject.Inject

class GiphyService @Inject constructor() {

    @Inject lateinit var giphyRepository: GiphyRepository

    suspend fun searchGifs(query: String, offset: Int): GiphyPagedResponse {
        val list: MutableList<GifItem> = mutableListOf()
        val json: JsonObject = giphyRepository.getGifs(query, offset) as JsonObject

        if (!json.has("data")) {
            throw EmptyBodyException("Oops, server is temporarily unavailable. Please try again later.")
        }

        json.getAsJsonArray("data").forEach {
            val gif: JsonObject = it.asJsonObject
            val images: JsonObject = gif.getAsJsonObject("images")
            val original: JsonObject = images.getAsJsonObject("original")
            val url: String = original["url"].asString
            val webp: String? = original["webp"]?.asString
            list.add(GifItem(url, webp))
        }

        val hasPagination = json.has("pagination")
        val total = if (hasPagination) json["pagination"].asJsonObject["total_count"].asInt else 0
        val currentOffset = if (hasPagination) json["pagination"].asJsonObject["offset"].asInt else 0

        return GiphyPagedResponse(
            list,
            nextPage = if (list.isNotEmpty() && hasPagination) currentOffset + GiphyNetworkClient.PAGE_SIZE else 0,
            total,
        )
    }
}