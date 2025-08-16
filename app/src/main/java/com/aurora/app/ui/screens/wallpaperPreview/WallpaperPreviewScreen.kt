package com.aurora.app.ui.screens.wallpaperPreview

import android.app.WallpaperManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.VideoSettings
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.ImageLoader
import coil3.request.ImageRequest
import coil3.request.allowHardware
import coil3.request.crossfade
import coil3.toBitmap
import com.aurora.app.R
import com.aurora.app.designsystem.theme.Orange
import com.aurora.app.designsystem.theme.Purple1
import com.aurora.app.designsystem.theme.Purple2
import com.aurora.app.designsystem.theme.Purple3
import com.aurora.app.designsystem.theme.Purple4
import com.aurora.app.service.VideoLiveWallpaperService
import com.aurora.app.ui.components.AuroraImage
import com.aurora.app.ui.components.ModernTopBar
import com.aurora.app.ui.components.VideoPreviewPlayer
import com.aurora.app.ui.navigation.ScreenTransition
import com.aurora.app.ui.screens.wallpaper.ModernSnackbar
import com.aurora.app.utils.Constants
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.delay
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

    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var isLoading by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var showFullscreen by remember { mutableStateOf(false) }
    var showApplyDialog by remember { mutableStateOf(false) }
    var isFavorite by remember { mutableStateOf(false) }

    if (showSuccessDialog) {
        ModernSuccessDialog(
            onDismiss = {
                isLoading = false
                showSuccessDialog = false
                navigator.navigateUp()
            }
        )
    }

    if (showErrorDialog) {
        ModernErrorDialog(
            onDismiss = {
                isLoading = false
                showErrorDialog = false
                navigator.navigateUp()
            }
        )
    }

    val context = LocalContext.current

    if (showApplyDialog) {
        ModernApplyDialog(
            extension = extension,
            onDismiss = { showApplyDialog = false },
            onApply = { applyType ->
                showApplyDialog = false
                // Handle apply logic here based on type
                when (extension) {
                    "mp4" -> {
                        applyLiveWallpaper(context = context, file = state.file!!)
                    }

                    "webp" -> {
                        isLoading = true
                        state.file?.let { file ->
                            setWallpaperFromUrl(
                                context = context,
                                data = file,
                                onSuccess = {
                                    isLoading = false
                                    showSuccessDialog = true
                                },
                                onError = {
                                    isLoading = false
                                    showErrorDialog = true
                                }
                            )
                        }
                    }
                }
            }
        )
    }

    val transition = updateTransition(targetState = showFullscreen, label = "FullscreenTransition")
    val topBarAlpha by transition.animateFloat(label = "TopBarAlpha") { fullscreen ->
        if (fullscreen) 0f else 1f
    }
    val bottomBarAlpha by transition.animateFloat(label = "BottomBarAlpha") { fullscreen ->
        if (fullscreen) 0f else 1f
    }

    Scaffold(
        topBar = {
            ModernTopBar(
                text = "Preview Wallpaper",
                onNavigationClick = { navigator.navigateUp() },
                modifier = Modifier.graphicsLayer { alpha = topBarAlpha }
            )
        }
    ) { contentPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
        ) {

            AnimatedPreviewBackground()

            Column(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .clickable { showFullscreen = !showFullscreen }
                ) {
                    if (state.file != null) {
                        ModernWallpaperPreview(
                            file = state.file!!,
                            extension = extension,
                            isFullscreen = showFullscreen,
                            onToggleFullscreen = { showFullscreen = !showFullscreen }
                        )
                    } else {
                        ModernLoadingPreview()
                    }
                }
            }

            ModernPreviewBottomBar(
                isFavorite = isFavorite,
                isLoading = isLoading,
                extension = extension,
                onFavoriteClick = {
                    isFavorite = !isFavorite
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            if (isFavorite) "Added to favorites ❤️" else "Removed from favorites"
                        )
                    }
                },
                onDownloadClick = {
                    scope.launch {
                        snackbarHostState.showSnackbar("Wallpaper downloaded! 📱")
                    }
                },
                onApplyClick = { showApplyDialog = true },
                modifier = Modifier
                    .graphicsLayer { alpha = bottomBarAlpha }
                    .align(Alignment.BottomCenter)
            )
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier,
            snackbar = { snackbarData ->
                ModernSnackbar(snackbarData = snackbarData)
            }
        )
    }

}

@Composable
fun ModernLoadingPreview() {
    val infiniteTransition = rememberInfiniteTransition(label = "LoadingAnimation")

    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "PulseAlpha"
    )

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "Rotation"
    )

    val shimmerTranslate by infiniteTransition.animateFloat(
        initialValue = -200f,
        targetValue = 200f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ShimmerTranslate"
    )

    Card(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .graphicsLayer { alpha = pulseAlpha },
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.1f)
        ),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.White.copy(alpha = 0.1f),
                                Color.Transparent
                            ),
                            start = Offset(shimmerTranslate - 100f, shimmerTranslate - 100f),
                            end = Offset(shimmerTranslate + 100f, shimmerTranslate + 100f)
                        )
                    )
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(32.dp)
            ) {
                // Outer ring with rotation
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .graphicsLayer { rotationZ = rotation },
                    contentAlignment = Alignment.Center
                ) {
                    // Outer progress ring
                    CircularProgressIndicator(
                        modifier = Modifier.size(120.dp),
                        color = Color.White.copy(alpha = 0.3f),
                        strokeWidth = 4.dp,
                        trackColor = Color.Transparent
                    )

                    // Inner progress ring with opposite rotation
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .graphicsLayer { rotationZ = -rotation * 1.5f },
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(80.dp),
                            color = Color.White.copy(alpha = 0.6f),
                            strokeWidth = 3.dp,
                            trackColor = Color.Transparent
                        )

                        // Center icon
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(
                                    Color.White.copy(alpha = 0.15f),
                                    CircleShape
                                )
                                .border(1.dp, Color.White.copy(alpha = 0.3f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Wallpaper,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                tint = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Loading text with typewriter effect
                LoadingText()

                Spacer(modifier = Modifier.height(16.dp))

                // Progress dots
                LoadingDots()

                Spacer(modifier = Modifier.height(24.dp))

                // Loading tips
                LoadingTip()
            }
        }
    }
}

@Composable
fun LoadingText() {
    val loadingTexts = listOf(
        "Loading wallpaper...",
        "Preparing preview...",
        "Almost ready...",
        "Getting things ready..."
    )

    var currentTextIndex by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(2000)
            currentTextIndex = (currentTextIndex + 1) % loadingTexts.size
        }
    }

    AnimatedContent(
        targetState = loadingTexts[currentTextIndex],
        transitionSpec = {
            slideInVertically(
                initialOffsetY = { it },
                animationSpec = tween(300)
            ) + fadeIn(animationSpec = tween(300)) togetherWith
                    slideOutVertically(
                        targetOffsetY = { -it },
                        animationSpec = tween(300)
                    ) + fadeOut(animationSpec = tween(300))
        },
        label = "LoadingTextAnimation"
    ) { text ->
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium.copy(
                color = Color.White.copy(alpha = 0.9f),
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        )
    }
}

@Composable
fun LoadingDots() {
    val infiniteTransition = rememberInfiniteTransition(label = "DotsAnimation")

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(3) { index ->
            val alpha by infiniteTransition.animateFloat(
                initialValue = 0.3f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(600, delayMillis = index * 200),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "DotAlpha$index"
            )

            val scale by infiniteTransition.animateFloat(
                initialValue = 0.7f,
                targetValue = 1.2f,
                animationSpec = infiniteRepeatable(
                    animation = tween(600, delayMillis = index * 200),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "DotScale$index"
            )

            Box(
                modifier = Modifier
                    .size(12.dp)
                    .graphicsLayer {
                        this.alpha = alpha
                        scaleX = scale
                        scaleY = scale
                    }
                    .background(
                        Color.White.copy(alpha = 0.8f),
                        CircleShape
                    )
            )
        }
    }
}

@Composable
fun LoadingTip() {
    val tips = listOf(
        "💡 Tip: Tap to preview fullscreen",
        "🔋 Live wallpapers use more battery",
        "📱 Try different wallpaper categories",
        "⭐ Add favorites for quick access",
        "🎨 Customize your home screen"
    )

    var currentTipIndex by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(3000)
            currentTipIndex = (currentTipIndex + 1) % tips.size
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.1f)
        ),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f))
    ) {
        AnimatedContent(
            targetState = tips[currentTipIndex],
            transitionSpec = {
                slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(400)
                ) + fadeIn(animationSpec = tween(400)) togetherWith
                        slideOutHorizontally(
                            targetOffsetX = { -it },
                            animationSpec = tween(400)
                        ) + fadeOut(animationSpec = tween(400))
            },
            label = "TipAnimation"
        ) { tip ->
            Text(
                text = tip,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color.White.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            )
        }
    }
}

@Composable
fun ModernApplyDialog(
    extension: String,
    onDismiss: () -> Unit,
    onApply: (String) -> Unit
) {
    var selectedOption by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(24.dp),
        title = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Icon based on wallpaper type
                Icon(
                    imageVector = if (extension == "mp4") Icons.Default.VideoSettings else Icons.Default.Wallpaper,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = if (extension == "mp4") "Apply Live Wallpaper" else "Apply Wallpaper",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    textAlign = TextAlign.Center
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                if (extension == "mp4") {
                    // Live wallpaper warning
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Orange.copy(alpha = 0.1f)
                        ),
                        border = BorderStroke(1.dp, Orange.copy(alpha = 0.3f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = Orange,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Battery Usage",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        color = Orange
                                    )
                                )
                                Text(
                                    text = "Live wallpapers may use more battery power",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "This will set a live wallpaper on your home screen. Continue?",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                        ),
                        textAlign = TextAlign.Center
                    )
                } else {
                    // Static wallpaper options
                    Text(
                        text = "Choose where to apply this wallpaper:",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                        ),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Apply options for static wallpapers
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        ApplyOptionCard(
                            title = "Home Screen",
                            description = "Set as home screen wallpaper",
                            icon = Icons.Default.Home,
                            isSelected = selectedOption == "Home Screen",
                            onClick = {
                                selectedOption = "Home Screen"
                            }
                        )

                        ApplyOptionCard(
                            title = "Lock Screen",
                            description = "Set as lock screen wallpaper",
                            icon = Icons.Default.Lock,
                            isSelected = selectedOption == "Lock Screen",
                            onClick = {
                                selectedOption = "Lock Screen"
                            }
                        )

                        ApplyOptionCard(
                            title = "Both",
                            description = "Set as both home and lock screen",
                            icon = Icons.Default.Wallpaper,
                            isSelected = selectedOption == "Both",
                            onClick = {
                                selectedOption = "Both"
                            }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = if (extension == "mp4") Arrangement.End else Arrangement.SpaceBetween
            ) {
                if (extension != "mp4") {
                    // Cancel button for static wallpapers
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                        )
                    ) {
                        Text(
                            "Cancel",
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))
                }

                // Apply button
                Button(
                    onClick = {
                        if (extension == "mp4") {
                            onApply("Live Wallpaper")
                        } else {
                            if (selectedOption.isNotEmpty()) {
                                onApply(selectedOption)
                            }
                        }
                    },
                    enabled = extension == "mp4" || selectedOption.isNotEmpty(),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                    ),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 4.dp,
                        pressedElevation = 8.dp
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            "Apply",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }

                if (extension == "mp4") {
                    Spacer(modifier = Modifier.width(12.dp))

                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                        )
                    ) {
                        Text(
                            "Cancel",
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        },
        dismissButton = null // We handle buttons in confirmButton
    )
}

@Composable
fun ApplyOptionCard(
    title: String,
    description: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val animatedColor by animateColorAsState(
        targetValue = if (isSelected)
            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
        else
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
        animationSpec = tween(200),
        label = "CardColor"
    )

    val borderColor by animateColorAsState(
        targetValue = if (isSelected)
            MaterialTheme.colorScheme.primary
        else
            MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
        animationSpec = tween(200),
        label = "BorderColor"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = animatedColor
        ),
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = borderColor
        )
    ) {
        Row(
            modifier = Modifier
                .clickable { onClick() }
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        if (isSelected)
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                        else
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = if (isSelected)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Text content
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = if (isSelected)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurface
                    )
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                )
            }

            // Selection indicator
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Selected",
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .border(
                            2.dp,
                            MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                            CircleShape
                        )
                )
            }
        }
    }
}

// Helper function for applying live wallpaper
private fun applyLiveWallpaper(context: Context, file: File) {
    try {
        // Save video path to SharedPreferences
        saveVideoPath(context = context, path = file.absolutePath)

        // Create intent to open live wallpaper picker
        val intent = Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER).apply {
            putExtra(
                WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                ComponentName(context, VideoLiveWallpaperService::class.java)
            )
            // Add flags to ensure proper behavior
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }

        // Check if the intent can be resolved
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
            Timber.d("applyLiveWallpaper: Live wallpaper picker opened successfully")
        } else {
            // Fallback: try to set wallpaper directly if picker is not available
            Timber.w("applyLiveWallpaper: Live wallpaper picker not available, trying direct approach")

            // Alternative approach for some devices
            val fallbackIntent = Intent().apply {
                action = "android.service.wallpaper.CHANGE_LIVE_WALLPAPER"
                putExtra(
                    "android.service.wallpaper.extra.LIVE_WALLPAPER_COMPONENT",
                    ComponentName(context, VideoLiveWallpaperService::class.java)
                )
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            if (fallbackIntent.resolveActivity(context.packageManager) != null) {
                context.startActivity(fallbackIntent)
            } else {
                throw Exception("Live wallpaper picker not available on this device")
            }
        }

    } catch (e: Exception) {
        Timber.e(e, "applyLiveWallpaper: Failed to apply live wallpaper")
        throw e
    }
}


@Composable
fun AnimatedPreviewBackground() {
    val infiniteTransition = rememberInfiniteTransition(label = "BackgroundAnimation")

    val gradientOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "GradientOffset"
    )

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
                    ),
                    startY = gradientOffset * 200f,
                    endY = Float.POSITIVE_INFINITY
                )
            )
    )
}

@Composable
fun ModernWallpaperPreview(
    file: File,
    extension: String,
    isFullscreen: Boolean,
    onToggleFullscreen: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isFullscreen) 1.05f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "PreviewScale"
    )

    Card(
        modifier = Modifier
            .fillMaxSize()
            .padding(if (isFullscreen) 0.dp else 20.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable { onToggleFullscreen() },
        shape = if (isFullscreen) RectangleShape else RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isFullscreen) 0.dp else 20.dp
        ),
        border = if (!isFullscreen) BorderStroke(2.dp, Color.White.copy(alpha = 0.2f)) else null
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Main wallpaper content
            when (extension) {
                "webp" -> {
                    AuroraImage(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(if (isFullscreen) RectangleShape else RoundedCornerShape(28.dp)),
                        image = file.absolutePath,
                        onClick = { },
                    )
                }

                else -> {
                    VideoPreviewPlayer(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(if (isFullscreen) RectangleShape else RoundedCornerShape(28.dp)),
                        file = file
                    )
                }
            }

            if (extension == "mp4" && !isFullscreen) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(24.dp)
                        .background(
                            Color.Red.copy(alpha = 0.9f),
                            RoundedCornerShape(20.dp)
                        )
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        // Pulsing live dot
                        val infiniteTransition = rememberInfiniteTransition(label = "LiveDot")
                        val alpha by infiniteTransition.animateFloat(
                            initialValue = 0.4f,
                            targetValue = 1f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(1000),
                                repeatMode = RepeatMode.Reverse
                            ),
                            label = "LiveDotAlpha"
                        )

                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .background(Color.White.copy(alpha = alpha), CircleShape)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "LIVE",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                    }
                }
            }

            if (!isFullscreen) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(24.dp)
                        .background(
                            Color.Black.copy(alpha = 0.6f),
                            RoundedCornerShape(16.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = if (extension == "mp4") "4K Video" else "HD Image",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }

            if (isFullscreen) {
                IconButton(
                    onClick = onToggleFullscreen,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(24.dp)
                        .size(56.dp)
                        .background(
                            Color.Black.copy(alpha = 0.6f),
                            CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Exit fullscreen",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ModernPreviewBottomBar(
    isFavorite: Boolean,
    isLoading: Boolean,
    extension: String,
    onFavoriteClick: () -> Unit,
    onDownloadClick: () -> Unit,
    onApplyClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(20.dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.15f)
        ),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
//            ModernActionButton(
//                icon = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
//                label = "Favorite",
//                onClick = onFavoriteClick,
//                tint = if (isFavorite) Color.Red else Color.White
//            )

//            ModernActionButton(
//                icon = Icons.Default.Download,
//                label = "Download",
//                onClick = onDownloadClick
//            )

            Button(
                onClick = onApplyClick,
                enabled = !isLoading,
                modifier = Modifier
                    .height(56.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = MaterialTheme.colorScheme.primary,
                    disabledContainerColor = Color.White.copy(alpha = 0.5f)
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 8.dp,
                    pressedElevation = 12.dp
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = if (extension == "mp4") Icons.Default.VideoSettings else Icons.Default.Wallpaper,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Apply",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
        }
    }
}

private fun saveVideoPath(context: Context, path: String) {
    Timber.e("saveVideoPath: Saving video path: $path")
    val prefs = context.getSharedPreferences(Constants.APP_PREFERENCES, Context.MODE_PRIVATE)
    prefs.edit().putString(Constants.WALLPAPER_KEY, path).apply()
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
        .allowHardware(false)
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

@Composable
fun ModernSuccessDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(24.dp),
        title = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = Color.Green
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    stringResource(id = R.string.wallpaper_set_successfully_title),
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    textAlign = TextAlign.Center
                )
            }
        },
        text = {
            Text(
                stringResource(id = R.string.wallpaper_set_successfully_message),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(stringResource(id = R.string.back_to_home))
            }
        }
    )
}

@Composable
fun ModernErrorDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(24.dp),
        title = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.ErrorOutline,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    stringResource(id = R.string.wallpaper_setting_failed_title),
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    textAlign = TextAlign.Center
                )
            }
        },
        text = {
            Text(
                stringResource(id = R.string.wallpaper_setting_failed_message),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text(stringResource(id = R.string.back_to_home))
            }
        }
    )
}