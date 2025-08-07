package com.aurora.app.ui.screens.wallpaperPreview

import android.app.WallpaperManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.ImageLoader
import coil3.request.ImageRequest
import coil3.request.allowHardware
import coil3.request.crossfade
import coil3.toBitmap
import com.aurora.app.R
import com.aurora.app.service.VideoLiveWallpaperService
import com.aurora.app.ui.components.AuroraTopBar
import com.aurora.app.ui.components.VideoPreviewPlayer
import com.aurora.app.ui.components.button.AuroraButton
import com.aurora.app.ui.navigation.ScreenTransition
import com.aurora.app.utils.Constants
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Destination<RootGraph>(style = ScreenTransition::class)
@Composable
fun WallpaperPreviewScreen(
    wallpaperId: String,
    extension: String,
    wallpaperUrl: String,
    navigator: DestinationsNavigator,
    viewModel: WallpaperPreviewViewModel = hiltViewModel()
) {

    LaunchedEffect(wallpaperId) {
        viewModel.loadWallpaper(
            wallpaperId = wallpaperId,
            extension = extension,
            wallpaperUrl = wallpaperUrl
        )
    }

    var isLoading by remember { mutableStateOf(false) }

    var showSuccessDialog by remember { mutableStateOf(false) }
    if (showSuccessDialog) {
        SuccessDialog(onDismiss = {
            isLoading = false; showSuccessDialog = false; navigator.navigateUp()
        })
    }

    var showErrorDialog by remember { mutableStateOf(false) }
    if (showErrorDialog) {
        ErrorDialog(onDismiss = {
            isLoading = false; showErrorDialog = false; navigator.navigateUp()
        })
    }

    val context = LocalContext.current
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            AuroraTopBar(
                text = "Wallpaper Preview",
                navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
                onNavigationClick = { navigator.navigateUp() }
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        content = { paddingValues ->

            Box(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            ) {
                if (state.file != null) {

                    Timber.e("WallpaperPreviewScreen: File path: ${state.file!!.absolutePath}")
                    VideoPreviewPlayer(
                        modifier = Modifier,
                        file = state.file!!
                    )
                    AuroraButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp, horizontal = 16.dp)
                            .align(alignment = Alignment.BottomCenter),
                        text = "Apply",
                        loading = isLoading,
                        onClick = {
                            when (extension) {
                                "mp4" -> {
                                    saveVideoPath(
                                        context = context,
                                        path = state.file!!.path
                                    )
                                    val intent =
                                        Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER).apply {
                                            putExtra(
                                                WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                                                ComponentName(
                                                    context,
                                                    VideoLiveWallpaperService::class.java
                                                )
                                            )
                                        }
                                    context.startActivity(intent)
                                }

                                "webp" -> {
                                    isLoading = true;
                                    setWallpaperFromUrl(context, state.file!!,
                                        onSuccess = { isLoading = false; showSuccessDialog = true },
                                        onError = { isLoading = false; showErrorDialog = true }
                                    )
                                }

                                else -> {
                                    scope.launch {
                                        snackbarHostState.showSnackbar(
                                            message = "Unsupported wallpaper format: $extension",
                                            duration = androidx.compose.material3.SnackbarDuration.Short
                                        )
                                        isLoading = false
                                    }
                                }
                            }

                        }
                    )
                }

            }
        }
    )
}

private fun saveVideoPath(context: Context, path: String) {
    Timber.e("saveVideoPath: Saving video path: $path")
    val prefs = context.getSharedPreferences(Constants.APP_PREFERENCES, Context.MODE_PRIVATE)
    prefs.edit().putString(Constants.WALLPAPER_KEY, path).apply()
}


@Composable
fun SuccessDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text(stringResource(id = R.string.wallpaper_set_successfully_title)) },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(stringResource(id = R.string.wallpaper_set_successfully_message))
            }
        },
        confirmButton = {
            Button(onClick = {
                onDismiss()
            }) {
                Text(stringResource(id = R.string.back_to_home))
            }
        },
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun ErrorDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text(stringResource(id = R.string.wallpaper_setting_failed_title)) },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(stringResource(id = R.string.wallpaper_setting_failed_message))
            }
        },
        confirmButton = {
            Button(onClick = {
                onDismiss()
            }) {
                Text(stringResource(id = R.string.back_to_home))
            }
        },
        modifier = Modifier.fillMaxWidth()
    )
}

fun setWallpaperFromUrl(
    context: Context,
    data: File,
    onSuccess: () -> Unit,
    onError: () -> Unit
) {
    Timber.d("setWallpaperFromUrl: Starting to load image from: ${data.path}")

    val wallpaperManager = WallpaperManager.getInstance(context)
    val imageLoader = ImageLoader.Builder(context)
        .crossfade(true)
        .build()

    val request = ImageRequest.Builder(context)
        .data(data)
        .allowHardware(false) // Required for .webp on older APIs
        .target(
            onSuccess = { result ->
                Timber.d("setWallpaperFromUrl: Image loaded successfully: ${result.size}")

                val bitmap = result.toBitmap()

                try {
                    wallpaperManager.setBitmap(bitmap)
                    onSuccess.invoke()
                    Timber.d("setWallpaperFromUrl: Wallpaper set successfully")
                } catch (e: Exception) {
                    Timber.e(e, "setWallpaperFromUrl: Failed to set wallpaper")
                    onError.invoke()
                }
            },
            onError = { error ->
                Timber.e("setWallpaperFromUrl: Failed to load image: $error")
                onError.invoke()
            }
        )
        .build()

    imageLoader.enqueue(request)
}
