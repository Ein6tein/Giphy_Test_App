package com.chernishenko.giphytestapp.repositories

import com.chernishenko.giphytestapp.exceptions.EmptyBodyException
import com.chernishenko.giphytestapp.exceptions.RequestFailedException
import com.chernishenko.giphytestapp.network.GiphyNetworkClient
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Response
import javax.inject.Inject

class GiphyRepository @Inject constructor() {

    @Inject lateinit var giphyClient: GiphyNetworkClient

    suspend fun getGifs(query: String, offset: Int) : Any? {
        return withContext(Dispatchers.IO) {
            val response: Response = giphyClient.getGifs(query, offset)
            if (response.isSuccessful) {
                val body = response.body
                if (body != null) {
                    val stringBody = body.string()
                    if (stringBody.isNotBlank()) {
                        return@withContext Gson().fromJson(stringBody, JsonObject::class.java)
                    } else {
                        throw EmptyBodyException("No GIFs found for that query")
                    }
                } else {
                    throw EmptyBodyException("No GIFs found for that query")
                }
            } else {
                throw RequestFailedException()
            }
        }
    }
}