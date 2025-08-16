package com.aurora.app.ui.screens.horoscope

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aurora.app.data.remote.response.HoroscopeData
import com.aurora.app.data.remote.response.ZodiacSign
import com.aurora.app.designsystem.theme.Horoscope1
import com.aurora.app.designsystem.theme.Horoscope2
import com.aurora.app.designsystem.theme.Horoscope3
import com.aurora.app.ui.components.ModernTopBar
import com.aurora.app.ui.navigation.ScreenTransition
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@OptIn(ExperimentalMaterial3Api::class)
@Destination<RootGraph>(style = ScreenTransition::class)
@Composable
fun HoroscopeScreen(
    navigator: DestinationsNavigator,
    viewModel: HoroscopeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Error handling
    uiState.error?.let { error ->
        LaunchedEffect(error) {
            // Show snackbar or handle error
            viewModel.clearError()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Horoscope1,
                        Horoscope2,
                        Horoscope3
                    )
                )
            )
    ) {
        // Animated background particles
        AnimatedBackgroundParticles()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {

            ModernTopBar(
                text = "Horoscope",
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent),
                onNavigationClick = {
                    navigator.navigateUp()
                }
            )

            HoroscopeHeader(
                currentSign = uiState.selectedZodiacSign,
                availableSigns = uiState.zodiacSigns,
                horoscopeData = uiState.horoscopeData,
                onSignSelected = viewModel::selectZodiacSign
            )

            // Tab Row
            GlassTabRow(
                selectedTab = uiState.selectedTab,
                onTabSelected = viewModel::selectTab
            )

            // Loading or Content
            Box(modifier = Modifier.weight(1f)) {
                when {
                    uiState.isLoading -> {
                        LoadingContent()
                    }

                    uiState.horoscopeData != null -> {
                        HoroscopeContent(
                            horoscope = uiState.horoscopeData!!,
                            selectedTab = uiState.selectedTab
                        )
                    }

                    uiState.error != null -> {
                        ErrorContent(
                            error = uiState.error!!,
                            onRetry = viewModel::refreshHoroscope
                        )
                    }

                    else -> {
                        EmptyContent()
                    }
                }
            }
        }

        // Floating refresh button
        FloatingActionButton(
            onClick = viewModel::refreshHoroscope,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = Color.Transparent,
            elevation = FloatingActionButtonDefaults.elevation(0.dp)
        ) {
            GlassContainer {
                Icon(
                    Icons.Default.Refresh,
                    contentDescription = "Refresh",
                    tint = Color.White,
                    modifier = Modifier
                        .padding(16.dp)
                        .then(
                            if (uiState.isLoading) {
                                Modifier.rotate(
                                    rememberInfiniteTransition(label = "refresh_rotation")
                                        .animateFloat(
                                            initialValue = 0f,
                                            targetValue = 360f,
                                            animationSpec = infiniteRepeatable(
                                                animation = tween(1000, easing = LinearEasing),
                                                repeatMode = RepeatMode.Restart
                                            ),
                                            label = "refresh_rotation"
                                        ).value
                                )
                            } else Modifier
                        )
                )
            }
        }

        // Error Snackbar
        uiState.error?.let { error ->
            Snackbar(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                action = {
                    TextButton(onClick = viewModel::clearError) {
                        Text("ठीक है", color = Color.White)
                    }
                }
            ) {
                Text(error, color = Color.White)
            }
        }
    }
}

@Composable
fun HoroscopeHeader(
    currentSign: ZodiacSign?,
    availableSigns: List<ZodiacSign>,
    horoscopeData: HoroscopeData?,
    onSignSelected: (ZodiacSign) -> Unit
) {
    var showDropdown by remember { mutableStateOf(false) }

    GlassContainer(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Zodiac Sign Selector
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.clickable { showDropdown = true }
            ) {
                AnimatedZodiacIcon(currentSign?.englishName ?: "aries")

                Column {
                    Text(
                        text = currentSign?.hindiName ?: "राशि चुनें",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    Text(
                        text = "बदलने के लिए टैप करें",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    )
                }

                Icon(
                    Icons.Default.ArrowDropDown,
                    contentDescription = "Change Sign",
                    tint = Color.White
                )
            }

            // Dropdown Menu
            DropdownMenu(
                expanded = showDropdown,
                onDismissRequest = { showDropdown = false },
                modifier = Modifier.background(
                    Color.Black.copy(alpha = 0.8f),
                    RoundedCornerShape(12.dp)
                )
            ) {
                availableSigns.forEach { sign ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = sign.hindiName,
                                color = Color.White
                            )
                        },
                        onClick = {
                            onSignSelected(sign)
                            showDropdown = false
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Date
            horoscopeData?.let { horoscope ->
                Box(
                    modifier = Modifier
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFFFF6B6B).copy(alpha = 0.3f),
                                    Color(0xFF4ECDC4).copy(alpha = 0.3f)
                                )
                            ),
                            shape = RoundedCornerShape(20.dp)
                        )
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = horoscope.date,
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun HoroscopeContent(
    horoscope: HoroscopeData,
    selectedTab: Int
) {
    AnimatedContent(
        targetState = selectedTab,
        transitionSpec = {
            slideInHorizontally(
                initialOffsetX = { if (targetState > initialState) 300 else -300 }
            ) togetherWith slideOutHorizontally(
                targetOffsetX = { if (targetState > initialState) -300 else 300 }
            )
        },
        label = "tab_animation"
    ) { tab ->
        when (tab) {
            0 -> MainPredictionTab(horoscope)
            1 -> CategoriesTab(horoscope)
            2 -> HighlightsTab(horoscope)
        }
    }
}

@Composable
fun LoadingContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        GlassContainer(
            modifier = Modifier.padding(32.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(32.dp)
            ) {
                CircularProgressIndicator(
                    color = Color(0xFF4ECDC4),
                    strokeWidth = 3.dp,
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "राशिफल लोड हो रहा है...",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = Color.White.copy(alpha = 0.8f)
                    )
                )
            }
        }
    }
}

@Composable
fun ErrorContent(
    error: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        GlassContainer(
            modifier = Modifier.padding(32.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(32.dp)
            ) {
                Icon(
                    Icons.Default.Warning,
                    contentDescription = null,
                    tint = Color(0xFFFF9800),
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = error,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = Color.White.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onRetry,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4ECDC4)
                    )
                ) {
                    Text("पुनः प्रयास करें", color = Color.White)
                }
            }
        }
    }
}

@Composable
fun EmptyContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        GlassContainer(
            modifier = Modifier.padding(32.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(32.dp)
            ) {
                Text(
                    text = "कोई राशिफल उपलब्ध नहीं",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = Color.White.copy(alpha = 0.8f)
                    )
                )
            }
        }
    }
}

@Composable
fun AnimatedZodiacIcon(signName: String) {
    val zodiacEmojis = mapOf(
        "aries" to "♈", "taurus" to "♉", "gemini" to "♊",
        "cancer" to "♋", "leo" to "♌", "virgo" to "♍",
        "libra" to "♎", "scorpio" to "♏", "sagittarius" to "♐",
        "capricorn" to "♑", "aquarius" to "♒", "pisces" to "♓"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "zodiac_rotation")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Box(
        modifier = Modifier
            .size(40.dp)
            .rotate(rotation)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFFFD700),
                        Color(0xFFFF6B6B)
                    )
                ),
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = zodiacEmojis[signName] ?: "♈",
            style = MaterialTheme.typography.headlineSmall.copy(
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        )
    }
}

// Reuse other components from previous implementation
@Composable
fun GlassTabRow(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    val tabs = listOf("मुख्य", "विभाग", "मुख्य बातें")

    GlassContainer(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
        ) {
            tabs.forEachIndexed { index, title ->
                val isSelected = selectedTab == index
                val animatedWeight by animateFloatAsState(
                    targetValue = if (isSelected) 1.2f else 1f,
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                    label = "tab_weight"
                )

                Box(
                    modifier = Modifier
                        .weight(animatedWeight)
                        .clickable { onTabSelected(index) }
                        .background(
                            brush = if (isSelected) {
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        Color(0xFFFF6B6B).copy(alpha = 0.4f),
                                        Color(0xFF4ECDC4).copy(alpha = 0.4f)
                                    )
                                )
                            } else {
                                Brush.horizontalGradient(
                                    colors = listOf(Color.Transparent, Color.Transparent)
                                )
                            },
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = Color.White
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun GlassContainer(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.15f),
                        Color.White.copy(alpha = 0.05f)
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(1000f, 1000f)
                ),
                shape = RoundedCornerShape(16.dp)
            )
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.2f),
                        Color.Transparent
                    )
                ),
                shape = RoundedCornerShape(16.dp)
            )
    ) {
        content()
    }
}

@Composable
fun AnimatedBackgroundParticles() {
    val infiniteTransition = rememberInfiniteTransition(label = "particles")

    repeat(15) { index ->
        val offsetY by infiniteTransition.animateFloat(
            initialValue = (index * 100).toFloat(),
            targetValue = (index * 100 + 50).toFloat(),
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 3000 + index * 200,
                    easing = EaseInOutSine
                ),
                repeatMode = RepeatMode.Reverse
            ),
            label = "particle_$index"
        )

        val offsetX by infiniteTransition.animateFloat(
            initialValue = (index * 80 % 300).toFloat(),
            targetValue = (index * 80 % 300 + 30).toFloat(),
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 4000 + index * 150,
                    easing = EaseInOutSine
                ),
                repeatMode = RepeatMode.Reverse
            ),
            label = "particle_x_$index"
        )

        Box(
            modifier = Modifier
                .offset(x = offsetX.dp, y = offsetY.dp)
                .size((4 + index % 3).dp)
                .background(
                    Color.White.copy(alpha = 0.1f + (index % 3) * 0.05f),
                    CircleShape
                )
        )
    }
}

@Composable
fun MainPredictionTab(horoscope: HoroscopeData) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            GlassContainer {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "आज का मुख्य राशिफल",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = horoscope.mainPrediction.hindi,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = Color.White.copy(alpha = 0.9f),
                            lineHeight = 28.sp
                        )
                    )

                    // Show English translation if available
                    if (horoscope.mainPrediction.englishTranslation.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(16.dp))

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 8.dp),
                            color = Color.White.copy(alpha = 0.2f)
                        )

                        Text(
                            text = "English Translation:",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF4ECDC4)
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = horoscope.mainPrediction.englishTranslation,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color.White.copy(alpha = 0.8f),
                                lineHeight = 24.sp,
                                fontStyle = FontStyle.Italic
                            )
                        )
                    }
                }
            }
        }

        // Source information
        item {
            GlassContainer {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            tint = Color(0xFF4ECDC4),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "स्रोत: ${horoscope.source}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        )
                    }

                    Text(
                        text = horoscope.date,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun CategoriesTab(horoscope: HoroscopeData) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        horoscope.categoryPredictions.loveRelationship?.let { prediction ->
            item {
                CategoryCard(
                    title = "प्रेम और संबंध",
                    content = prediction.hindi,
                    englishContent = prediction.englishTranslation,
                    icon = Icons.Default.Favorite,
                    color = Color(0xFFFF6B6B)
                )
            }
        }

        horoscope.categoryPredictions.healthWellbeing?.let { prediction ->
            item {
                CategoryCard(
                    title = "स्वास्थ्य और कल्याण",
                    content = prediction.hindi,
                    englishContent = prediction.englishTranslation,
                    icon = Icons.Default.FavoriteBorder,
                    color = Color(0xFF4ECDC4)
                )
            }
        }

        horoscope.categoryPredictions.moneyFinance?.let { prediction ->
            item {
                CategoryCard(
                    title = "धन और वित्त",
                    content = prediction.hindi,
                    englishContent = prediction.englishTranslation,
                    icon = Icons.Default.AccountCircle,
                    color = Color(0xFFFFD93D)
                )
            }
        }

        horoscope.categoryPredictions.careerBusiness?.let { prediction ->
            item {
                CategoryCard(
                    title = "करियर और व्यापार",
                    content = prediction.hindi,
                    englishContent = prediction.englishTranslation,
                    icon = Icons.Default.Build,
                    color = Color(0xFF6BCF7F)
                )
            }
        }

        // If no categories available
        if (listOf(
                horoscope.categoryPredictions.loveRelationship,
                horoscope.categoryPredictions.healthWellbeing,
                horoscope.categoryPredictions.moneyFinance,
                horoscope.categoryPredictions.careerBusiness
            ).all { it == null }
        ) {
            item {
                GlassContainer {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.6f),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "इस राशि के लिए विस्तृत विभाग जानकारी उपलब्ध नहीं है।",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = Color.White.copy(alpha = 0.8f),
                                textAlign = TextAlign.Center
                            )
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun CategoryCard(
    title: String,
    content: String,
    englishContent: String = "",
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color
) {
    var isExpanded by remember { mutableStateOf(false) }

    GlassContainer {
        Column(
            modifier = Modifier
                .animateContentSize()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = color
                    ),
                    modifier = Modifier.weight(1f)
                )

                if (englishContent.isNotEmpty()) {
                    IconButton(
                        onClick = { isExpanded = !isExpanded },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = if (isExpanded) "Show Less" else "Show English",
                            tint = Color.White.copy(alpha = 0.7f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = content,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.White.copy(alpha = 0.85f),
                    lineHeight = 24.sp
                )
            )

            // Show English translation if expanded
            AnimatedVisibility(
                visible = isExpanded && englishContent.isNotEmpty(),
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column {
                    Spacer(modifier = Modifier.height(12.dp))

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        color = color.copy(alpha = 0.3f)
                    )

                    Text(
                        text = englishContent,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.White.copy(alpha = 0.75f),
                            lineHeight = 20.sp,
                            fontStyle = FontStyle.Italic
                        )
                    )
                }
            }
        }
    }
}


@Composable
fun HighlightsTab(horoscope: HoroscopeData) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Key Highlights Section
        if (horoscope.keyHighlights.isNotEmpty()) {
            item {
                HighlightSection(
                    title = "मुख्य बातें",
                    items = horoscope.keyHighlights,
                    icon = Icons.Default.Star,
                    color = Color(0xFFFFD700)
                )
            }
        }

        // Warnings and Advice Section
        if (horoscope.warningsAdvice.isNotEmpty()) {
            item {
                HighlightSection(
                    title = "सलाह और चेतावनी",
                    items = horoscope.warningsAdvice,
                    icon = Icons.Default.Warning,
                    color = Color(0xFFFF9800)
                )
            }
        }

        // Lucky Elements (if available)
        item {
            LuckyElementsCard()
        }

        // Share Option
        item {
            ShareHoroscopeCard(horoscope)
        }

        // If no highlights available
        if (horoscope.keyHighlights.isEmpty() && horoscope.warningsAdvice.isEmpty()) {
            item {
                GlassContainer {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.6f),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "इस राशि के लिए मुख्य बातें उपलब्ध नहीं हैं।",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = Color.White.copy(alpha = 0.8f),
                                textAlign = TextAlign.Center
                            )
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun HighlightSection(
    title: String,
    items: List<String>,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color
) {
    GlassContainer {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = color
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            items.forEachIndexed { index, item ->
                AnimatedVisibility(
                    visible = true,
                    enter = slideInVertically(
                        initialOffsetY = { 50 },
                        animationSpec = tween(
                            durationMillis = 300,
                            delayMillis = index * 100
                        )
                    ) + fadeIn(
                        animationSpec = tween(
                            durationMillis = 300,
                            delayMillis = index * 100
                        )
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 4.dp),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(color, CircleShape)
                                .padding(top = 8.dp)
                        )
                        Text(
                            text = item,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color.White.copy(alpha = 0.85f),
                                lineHeight = 20.sp
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LuckyElementsCard() {
    val luckyElements = listOf(
        "भाग्यशाली रंग: लाल, नारंगी" to Color(0xFFFF6B6B),
        "भाग्यशाली संख्या: 3, 9, 21" to Color(0xFFFFD93D),
        "भाग्यशाली दिशा: पूर्व" to Color(0xFF4ECDC4),
        "भाग्यशाली समय: सुबह 6-8 बजे" to Color(0xFF6BCF7F)
    )

    GlassContainer {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    Icons.Default.DateRange,
                    contentDescription = null,
                    tint = Color(0xFFFF6B6B),
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "आज के भाग्यशाली तत्व",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFF6B6B)
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            luckyElements.forEachIndexed { _, (element, color) ->
                Row(
                    modifier = Modifier.padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(color, CircleShape)
                    )
                    Text(
                        text = element,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun ShareHoroscopeCard(horoscope: HoroscopeData) {
    val context = LocalContext.current

    GlassContainer {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    // Share functionality
                    val shareText = """
                        ${horoscope.zodiacSign} - ${horoscope.date}
                        
                        ${horoscope.mainPrediction.hindi}
                        
                        - ${horoscope.source}
                    """.trimIndent()

                    val shareIntent = android.content.Intent().apply {
                        action = android.content.Intent.ACTION_SEND
                        type = "text/plain"
                        putExtra(android.content.Intent.EXTRA_TEXT, shareText)
                    }
                    context.startActivity(
                        android.content.Intent.createChooser(shareIntent, "राशिफल साझा करें")
                    )
                }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                Icons.Default.Share,
                contentDescription = "Share",
                tint = Color(0xFF4ECDC4),
                modifier = Modifier.size(24.dp)
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "राशिफल साझा करें",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
                Text(
                    text = "दोस्तों और परिवार के साथ साझा करें",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color.White.copy(alpha = 0.7f)
                    )
                )
            }

            Icon(
                Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.5f),
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
