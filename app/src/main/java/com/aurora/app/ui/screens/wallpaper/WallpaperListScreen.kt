package com.aurora.app.ui.screens.wallpaper

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aurora.app.R
import com.aurora.app.domain.model.wallpaper.WallpaperDto
import com.aurora.app.domain.model.wallpaper.WallpaperSectionView
import com.aurora.app.ui.components.AuroraImage
import com.aurora.app.ui.components.AuroraTopBar
import com.aurora.app.ui.components.ListVideoPlayer
import com.aurora.app.ui.navigation.ScreenTransition
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.WallpaperPreviewScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.valentinilk.shimmer.shimmer
import kotlinx.coroutines.launch
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class)
@Destination<RootGraph>(style = ScreenTransition::class)
@Composable
fun WallpaperListScreen(
    navigator: DestinationsNavigator,
    viewModel: WallpaperListViewModel = hiltViewModel()
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            AuroraTopBar(
                titleRes = R.string.app_name,
                navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
                onNavigationClick = { navigator.navigateUp() }
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        content = { paddingValues ->
            if (!uiState.isLoading && uiState.wallpaperSections.isEmpty()) {
                EmptyComposable(onEmptyAction = { navigator.navigateUp() })
            } else if (uiState.isLoading) {
                LoadingSection(Modifier.fillMaxSize())
            } else if (uiState.errorMessages.isNotEmpty()) {
                ErrorContainer(
                    message = "No Wallpapers, Try Refresh"
                ) { viewModel.refreshWallpapers() }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize()
                ) {
                    wallpaperSection(
                        wallpaperSections = uiState.wallpaperSections,
                        onClick = { wallpaper ->
                            Timber.e("Clicked on wallpaper: $wallpaper")
                            scope.launch {
                                val url = wallpaper.url ?: ""
                                if (url.isEmpty()) {
                                    Timber.w("Wallpaper URL is empty for ${wallpaper.ename}")
                                    snackbarHostState.showSnackbar("Wallpaper URL is empty")
                                } else {
                                    navigator.navigate(
                                        WallpaperPreviewScreenDestination(
                                            wallpaper.id,
                                            wallpaper.extension,
                                            url
                                        )
                                    )
                                }
                            }


                        }
                    )
                }
            }
        }
    )
}


@Composable
fun EmptyComposable(
    modifier: Modifier = Modifier,
    onEmptyAction: (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_empty_data),
            contentDescription = "Empty Data",
            modifier = Modifier.size(150.dp)
        )
        Text(
            text = stringResource(R.string.empty_message),
            modifier = Modifier.padding(top = 16.dp),
            style = MaterialTheme.typography.bodyMedium
        )
        if (onEmptyAction != null) {
            Button(
                onClick = { onEmptyAction() },
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text("Back")
            }
        }

    }
}

fun LazyListScope.wallpaperSection(
    wallpaperSections: List<WallpaperSectionView>,
    onClick: (WallpaperDto) -> Unit
) {
    items(wallpaperSections) { section ->
        Column(
            modifier = Modifier.wrapContentHeight()
        ) {
            Text(
                text = section.title,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Timber.d("Section '${section.title}' has ${section.wallpapers.size} wallpapers")

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(section.wallpapers, key = { it.id }) { wallpaper ->
                    if (wallpaper.url.isEmpty()) {
                        Timber.w("Wallpaper URL is empty for ${wallpaper.ename}")
                    } else {
                        when (wallpaper.extension) {
                            "webp" -> {
                                AuroraImage(
                                    modifier = Modifier
                                        .width(160.dp)
                                        .fillMaxHeight()
                                        .aspectRatio(2 / 3.8f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { onClick(wallpaper) },
                                    image = wallpaper.url,
                                    onClick = { onClick(wallpaper) }
                                )
                            }

                            else -> {
                                ListVideoPlayer(
                                    videoUrl = wallpaper.url,
                                    modifier = Modifier
                                        .width(160.dp)
                                        .fillMaxHeight()
                                        .aspectRatio(2 / 3.8f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { onClick(wallpaper) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LoadingSection(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .clip(shape = RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                .padding(vertical = 16.dp, horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                modifier = Modifier
                    .shimmer()
                    .size(124.dp),
                contentDescription = null,
                painter = painterResource(id = R.drawable.app_icon),
            )
            Text(
                text = "Loading",
                style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.primary)
            )
        }

    }
}

@Composable
fun ErrorContainer(modifier: Modifier = Modifier, message: String, onRetry: () -> Unit) {
    TextButton(
        onClick = onRetry,
        modifier.fillMaxSize()
    ) {
        Text(
            text = message,
            textAlign = TextAlign.Center
        )
    }
}
