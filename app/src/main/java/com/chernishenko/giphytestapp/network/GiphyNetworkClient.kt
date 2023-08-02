package com.chernishenko.giphytestapp.network

import com.chernishenko.giphytestapp.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import javax.inject.Inject

class GiphyNetworkClient @Inject constructor() {

    companion object {
        const val PAGE_SIZE = 25
    }

    private val baseUrl: String = "https://api.giphy.com/v1/gifs/search"
    private val apiKey: String = BuildConfig.API_KEY

    private val client: OkHttpClient = OkHttpClient().newBuilder().addInterceptor(HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }).build()

    init {
        HttpLoggingInterceptor()
    }

    fun getGifs(query: String, offset: Int) : Response {
        val request: Request = Request.Builder()
            .url("$baseUrl?api_key=$apiKey&q=$query&limit=$PAGE_SIZE&offset=$offset&rating=g&lang=en")
            .build()
        return client.newCall(request).execute()
    }
}