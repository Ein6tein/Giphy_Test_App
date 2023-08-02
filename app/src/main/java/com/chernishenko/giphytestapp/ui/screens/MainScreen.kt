package com.chernishenko.giphytestapp.ui.screens

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.KeyboardArrowUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import coil.ImageLoader
import com.chernishenko.giphytestapp.ui.theme.GiphyTestAppTheme
import com.chernishenko.giphytestapp.ui.theme.LargePadding
import com.chernishenko.giphytestapp.ui.widgets.GifImage
import com.chernishenko.giphytestapp.ui.widgets.SearchTextField
import com.chernishenko.giphytestapp.viewmodel.GifViewModel
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen(viewModel: GifViewModel, imageLoader: ImageLoader) {
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { _ ->
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            Column(modifier = Modifier.fillMaxSize().padding(all = LargePadding)) {
                val coroutineScope = rememberCoroutineScope()
//                val gridState = rememberLazyStaggeredGridState()
                val listState = rememberLazyListState()
                val showScrollToTop by remember {
                    derivedStateOf {
//                        gridState.firstVisibleItemIndex > 3
                        listState.firstVisibleItemIndex > 3
                    }
                }
                val list = viewModel.listData.collectAsLazyPagingItems()
                SearchTextField(
                    modifier = Modifier.fillMaxWidth(),
                    viewModel = viewModel
                )
                Spacer(modifier = Modifier.padding(top = LargePadding))
                Box(modifier = Modifier.fillMaxSize()) {
                    if (list.loadState.refresh is LoadState.Loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center),
                            color = MaterialTheme.colorScheme.primary,
                        )
                    } else {
                        if (list.itemCount == 0) {
                            Text(
                                text = "No results",
                                modifier = Modifier.align(Alignment.Center),
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                            )
                        } else {
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(LargePadding),
                                state = listState,
                            ) {
                                items(
                                    list.itemCount,
                                    key = { list[it]?.url ?: it }
                                ) {
                                    val item = list[it]
                                    GifImage(
                                        url = item!!.webp ?: item.url,
                                        contentDescription = "GIF",
                                        imageLoader = imageLoader,
                                    )
                                }
                            }
                            // It works, but it has a significant performance issue when scrolling up manually
                            /*LazyVerticalStaggeredGrid(
                                columns = StaggeredGridCells.Fixed(2),
                                contentPadding = PaddingValues(LargePadding),
                                verticalItemSpacing = LargePadding,
                                horizontalArrangement = Arrangement.spacedBy(LargePadding),
                                state = gridState,
                            ) {
                                items(
                                    list.itemCount,
                                    key = { list[it]?.url ?: it }
                                ) {
                                    val item = list[it]
                                    GifImage(
                                        url = item!!.webp ?: item.url,
                                        contentDescription = "GIF",
                                        imageLoader = imageLoader,
                                    )
                                }
                            }*/
                            // Issue and solution described here:
                            // https://stackoverflow.com/questions/67975569/why-cant-i-use-animatedvisibility-in-a-boxscope
                            this@Column.AnimatedVisibility(
                                visible = showScrollToTop,
                                modifier = Modifier.align(Alignment.BottomEnd),
                            ) {
                                FloatingActionButton(
                                    onClick = {
                                        coroutineScope.launch {
//                                            gridState.animateScrollToItem(index = 0)
                                            listState.animateScrollToItem(index = 0)
                                        }
                                    },
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    shape = MaterialTheme.shapes.small,
                                ) {
                                    Icon(
                                        Icons.Sharp.KeyboardArrowUp,
                                        contentDescription = "Scroll to top",
                                        tint = MaterialTheme.colorScheme.onPrimary
                                    )
                                }
                            }
                        }
                    }
                }
                if (list.loadState.refresh is LoadState.Error) {
                    LaunchedEffect(snackbarHostState) {
                        snackbarHostState.showSnackbar(list.loadState.source.toString())
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    GiphyTestAppTheme {
        MainScreen(GifViewModel(), ImageLoader.Builder(LocalContext.current).build())
    }
}