package com.aurora.app.ui.screens.wallpaper

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aurora.app.R
import com.aurora.app.designsystem.theme.Purple1
import com.aurora.app.designsystem.theme.Purple2
import com.aurora.app.designsystem.theme.Purple3
import com.aurora.app.designsystem.theme.Purple4
import com.aurora.app.domain.model.wallpaper.WallpaperDto
import com.aurora.app.domain.model.wallpaper.WallpaperSectionView
import com.aurora.app.ui.components.AuroraImage
import com.aurora.app.ui.components.ListVideoPlayer
import com.aurora.app.ui.components.ModernTopBar
import com.aurora.app.ui.navigation.ScreenTransition
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.SeeAllScreenDestination
import com.ramcosta.composedestinations.generated.destinations.WallpaperPreviewScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.valentinilk.shimmer.shimmer
import kotlinx.coroutines.delay
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
    val scrollState = rememberLazyListState()

    LaunchedEffect(Unit) {
        viewModel.fetchWallpapers()
    }

    Scaffold(
        topBar = {
            ModernTopBar(
                text = "Live Wallpapers",
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
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)) {

            AnimatedBackground()

            when {
                !uiState.isLoading && uiState.wallpaperSections.isEmpty() -> {
                    ModernEmptyState(onBackClick = { navigator.navigateUp() })
                }

                uiState.isLoading -> {
                    ModernLoadingSection(Modifier.fillMaxSize())
                }

                uiState.errorMessages.isNotEmpty() -> {
                    ModernErrorContainer(
                        message = "No Wallpapers Available",
                        onRetry = { viewModel.refreshWallpapers() }
                    )
                }

                else -> {
                    LazyColumn(
                        state = scrollState,
                        modifier = Modifier
                            .fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 100.dp)
                    ) {
                        item {
                            HeroSection(
                                modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
                            )
                        }

                        modernWallpaperSection(
                            wallpaperSections = uiState.wallpaperSections,
                            seeAllClick = {
                                navigator.navigate(SeeAllScreenDestination(it))
                            },
                            onClick = { wallpaper ->
                                Timber.e("Clicked on wallpaper: $wallpaper")
                                scope.launch {
                                    val url = wallpaper.url
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
        }
    }
}

@Composable
fun AnimatedBackground() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Purple1,
                        Purple2,
                        Purple3,
                        Purple4,
                    )
                )
            )
    )
}


@Composable
fun HeroSection(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Transform Your Screen",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = Color.White,
                shadow = Shadow(
                    color = Color.Black.copy(alpha = 0.3f),
                    offset = Offset(2f, 2f),
                    blurRadius = 4f
                )
            ),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Discover beautiful live wallpapers to personalize your device",
            style = MaterialTheme.typography.bodyLarge.copy(
                color = Color.White.copy(alpha = 0.9f),
                textAlign = TextAlign.Center
            ),
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Stats or featured section
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(3) { index ->
                StatCard(
                    icon = when (index) {
                        0 -> Icons.Default.Wallpaper
                        1 -> Icons.Default.Download
                        else -> Icons.Default.Star
                    },
                    title = when (index) {
                        0 -> "1000+"
                        1 -> "50K+"
                        else -> "4.8"
                    },
                    subtitle = when (index) {
                        0 -> "Wallpapers"
                        1 -> "Downloads"
                        else -> "Rating"
                    }
                )
            }
        }
    }
}

@Composable
fun StatCard(
    icon: ImageVector,
    title: String,
    subtitle: String
) {
    Card(
        modifier = Modifier
            .size(100.dp, 80.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.15f)
        ),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = Color.White.copy(alpha = 0.8f)
                )
            )
        }
    }
}

fun LazyListScope.modernWallpaperSection(
    wallpaperSections: List<WallpaperSectionView>,
    onClick: (WallpaperDto) -> Unit,
    seeAllClick: (WallpaperSectionView) -> Unit
) {
    itemsIndexed(wallpaperSections) { sectionIndex, section ->
        val animationDelay = sectionIndex * 100

        Column(
            modifier = Modifier
                .wrapContentHeight()
                .padding(horizontal = 20.dp)
                .animateContentSize()
        ) {
            // Section header with modern styling
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = section.title,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    Text(
                        text = "${section.wallpapers.size} wallpapers",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    )
                }

                TextButton(
                    onClick = { seeAllClick(section) },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color.White
                    )
                ) {
                    Text("See All")
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(start = 4.dp)
                            .size(16.dp)
                    )
                }
            }

            // Wallpaper grid with staggered animation
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(end = 20.dp)
            ) {
                itemsIndexed(
                    section.wallpapers,
                    key = { _, wallpaper -> wallpaper.id }) { index, wallpaper ->
                    if (wallpaper.url.isNotEmpty()) {
                        val itemAnimationDelay = animationDelay + (index * 50)

                        ModernWallpaperCard(
                            wallpaper = wallpaper,
                            onClick = { onClick(wallpaper) },
                            animationDelay = itemAnimationDelay
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun ModernWallpaperCard(
    wallpaper: WallpaperDto,
    onClick: () -> Unit,
    animationDelay: Int = 0
) {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(animationDelay.toLong())
        isVisible = true
    }

    val scale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.8f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "ScaleAnimation"
    )

    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(400),
        label = "AlphaAnimation"
    )

    Card(
        modifier = Modifier
            .width(180.dp)
            .height(280.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                this.alpha = alpha
            }
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp,
            pressedElevation = 12.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.1f)
        ),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f))
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            when (wallpaper.extension) {
                "webp" -> {
                    AuroraImage(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(20.dp)),
                        image = wallpaper.url,
                        onClick = onClick
                    )
                }

                else -> {
                    ListVideoPlayer(
                        videoUrl = wallpaper.url,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(20.dp))
                    )
                }
            }

            // Gradient overlay for better text visibility
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.7f)
                            ),
                            startY = 100f
                        )
                    )
            )

            // Wallpaper info
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {
                Text(
                    text = wallpaper.ename,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = if (wallpaper.extension == "webp") "Wallpaper" else "Live",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color.White.copy(alpha = 0.8f)
                    )
                )
            }

            // Live indicator for video wallpapers
            if (wallpaper.extension != "webp") {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                        .background(
                            Color.Red.copy(alpha = 0.8f),
                            RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "LIVE",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                }
            }

            // Download/heart icon
            IconButton(
                onClick = { /* Handle favorite */ },
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(8.dp)
                    .size(40.dp)
                    .background(
                        Color.White.copy(alpha = 0.2f),
                        CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun ModernEmptyState(onBackClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Animated empty icon
        Icon(
            imageVector = Icons.Default.Wallpaper,
            contentDescription = "No wallpapers",
            modifier = Modifier
                .size(120.dp)
                .alpha(0.6f),
            tint = Color.White
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "No Wallpapers Found",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Looks like there are no wallpapers available at the moment. Please try again later.",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = Color.White.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onBackClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = MaterialTheme.colorScheme.primary
            ),
            shape = RoundedCornerShape(25.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text(
                "Go Back",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )
        }
    }
}

@Composable
fun ModernLoadingSection(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Animated loading icon
            Icon(
                painter = painterResource(id = R.drawable.app_icon),
                contentDescription = null,
                modifier = Modifier
                    .size(80.dp)
                    .shimmer(),
                tint = Color.White.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Loading Beautiful Wallpapers...",
                style = MaterialTheme.typography.titleMedium.copy(
                    color = Color.White.copy(alpha = 0.9f)
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                modifier = Modifier
                    .width(200.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = Color.White,
                trackColor = Color.White.copy(alpha = 0.3f)
            )
        }
    }
}

@Composable
fun ModernErrorContainer(
    modifier: Modifier = Modifier,
    message: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.ErrorOutline,
            contentDescription = "Error",
            modifier = Modifier.size(80.dp),
            tint = Color.White.copy(alpha = 0.8f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = message,
            style = MaterialTheme.typography.titleLarge.copy(
                color = Color.White,
                textAlign = TextAlign.Center
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = MaterialTheme.colorScheme.primary
            ),
            shape = RoundedCornerShape(25.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Try Again")
        }
    }
}

@Composable
fun ModernSnackbar(snackbarData: SnackbarData) {
    Snackbar(
        snackbarData = snackbarData,
        shape = RoundedCornerShape(12.dp),
        containerColor = Color.White,
        contentColor = MaterialTheme.colorScheme.primary,
        actionColor = MaterialTheme.colorScheme.primary
    )
}