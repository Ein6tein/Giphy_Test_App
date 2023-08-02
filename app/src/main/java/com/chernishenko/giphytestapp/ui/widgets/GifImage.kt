package com.chernishenko.giphytestapp.ui.widgets

import android.graphics.drawable.Drawable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.AsyncImage

@Composable
fun GifImage(url: String, contentDescription: String? = null, imageLoader: ImageLoader) {
    var isLoading by remember { mutableStateOf(true) }
    var drawable: Drawable? by remember(url) { mutableStateOf(null) }
    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
    AsyncImage(
        modifier = Modifier.fillMaxWidth().wrapContentHeight(),
        model = drawable ?: url,
        contentDescription = contentDescription,
        imageLoader = imageLoader,
        contentScale = ContentScale.FillWidth,
        onLoading = {
            isLoading = true
        },
        onSuccess = {
            drawable = it.result.drawable
            isLoading = false
        },
    )
}
