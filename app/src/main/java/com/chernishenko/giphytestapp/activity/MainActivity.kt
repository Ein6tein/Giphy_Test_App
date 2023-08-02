package com.chernishenko.giphytestapp.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import coil.imageLoader
import com.chernishenko.giphytestapp.ui.screens.MainScreen
import com.chernishenko.giphytestapp.ui.theme.GiphyTestAppTheme
import com.chernishenko.giphytestapp.viewmodel.GifViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: GifViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            GiphyTestAppTheme {
                LaunchedEffect("") {
                    viewModel.run()
                }
                MainScreen(viewModel, imageLoader = imageLoader)
            }
        }
    }
}
