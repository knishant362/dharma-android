package com.aurora.app.ui.screens.wallpaper.seeAll

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aurora.app.domain.model.wallpaper.WallpaperSectionView
import com.aurora.app.ui.components.ModernTopBar
import com.aurora.app.ui.navigation.ScreenTransition
import com.aurora.app.ui.screens.wallpaper.AnimatedBackground
import com.aurora.app.ui.screens.wallpaper.ModernSnackbar
import com.aurora.app.ui.screens.wallpaper.ModernWallpaperCard
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.WallpaperPreviewScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Destination<RootGraph>(style = ScreenTransition::class)
@Composable
fun SeeAllScreen(
    section: WallpaperSectionView,
    navigator: DestinationsNavigator,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            ModernTopBar(
                text = section.title,
                onNavigationClick = { navigator.navigateUp() },
            )
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                snackbar = { snackbarData ->
                    ModernSnackbar(snackbarData = snackbarData)
                }
            )
        }
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            AnimatedBackground()

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                // Wallpaper items
                items(section.wallpapers.size) { index ->
                    val wallpaper = section.wallpapers[index]
                    if (wallpaper.url.isNotEmpty()) {
                        ModernWallpaperCard(
                            wallpaper = wallpaper,
                            onClick = {
                                scope.launch {
                                    val url = wallpaper.url
                                    if (url.isEmpty()) {
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
                            },
                            animationDelay = 0
                        )
                    }
                }

                item(span = { GridItemSpan(maxLineSpan) }) {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

