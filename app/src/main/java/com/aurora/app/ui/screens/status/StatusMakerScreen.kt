package com.aurora.app.ui.screens.status

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aurora.app.designsystem.theme.Horoscope1
import com.aurora.app.designsystem.theme.Horoscope2
import com.aurora.app.designsystem.theme.Horoscope3
import com.aurora.app.designsystem.theme.Purple4
import com.aurora.app.domain.model.dashboard.StatusDto
import com.aurora.app.ui.components.ListVideoPlayer
import com.aurora.app.ui.components.ModernTopBar
import com.aurora.app.ui.components.button.AuroraButton
import com.aurora.app.ui.navigation.ScreenTransition
import com.aurora.app.ui.screens.status.model.SocialStats
import com.aurora.app.ui.screens.status.model.UserProfile
import com.aurora.app.ui.screens.status.tools.overlay.DefaultOverlayProperties
import com.aurora.app.ui.screens.status.tools.overlay.OverlayPreview
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.VerticalPager
import com.google.accompanist.pager.rememberPagerState
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.StatusExportScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@OptIn(ExperimentalMaterial3Api::class)
@Destination<RootGraph>(style = ScreenTransition::class)
@Composable
fun StatusMakerScreen(
    navigator: DestinationsNavigator,
    viewModel: StatusMakerViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var currentPage by remember { mutableStateOf(0) }
    var showEditProfile by remember { mutableStateOf(false) }

    val pagerState = rememberPagerState(initialPage = currentPage)

    val overlayProperties = remember { DefaultOverlayProperties.currentStyles }
    var overlayStyleIndex by remember { mutableStateOf(0) }


    LaunchedEffect(pagerState.currentPage) {
        currentPage = pagerState.currentPage
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {

        if (state.statusVideos.isNotEmpty() && state.userProfile != null && state.socialStats != null) {
            VideoVerticalPager(
                state = state,
                pagerState = pagerState,
                overlaySection = {
                    val videoWidth = state.statusVideos[pagerState.currentPage].meta!!.w.toFloat()
                    val videoHeight = state.statusVideos[pagerState.currentPage].meta!!.h.toFloat()
                    val scale = videoWidth / videoHeight
                    val selectedOverlay = overlayProperties[overlayStyleIndex]
                    OverlayPreview(
                        scale = scale,
                        overlayProperties = selectedOverlay,
                        userProfile = state.userProfile!!,
                        modifier = Modifier
                    )

                },
                socialStatsLayout = {
                    SocialStatsRow(
                        socialStats = state.socialStats!!,
                        onPrevious = {
                            overlayStyleIndex =
                                if (overlayStyleIndex > 0) overlayStyleIndex - 1 else overlayProperties.size - 1

                        },
                        onNext = {
                            overlayStyleIndex = (overlayStyleIndex + 1) % overlayProperties.size
                        },
                        modifier = Modifier.padding(8.dp)
                    )
                }
            )
        } else {
            Text(
                text = "Loading...",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                modifier = Modifier.align(Alignment.Center)
            )
            return@Box
        }

        ModernTopBar(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding(),
            text = "Status Maker",
            onNavigationClick = {
                navigator.navigateUp()
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
        )

        if (showEditProfile) {
            EditProfileDialog(
                userProfile = state.userProfile!!,
                onDismiss = { showEditProfile = false },
                onSave = { updatedProfile ->
                    showEditProfile = false
                }
            )
        }

        Column(
            modifier = Modifier.align(Alignment.BottomStart)
        ) {
            AuroraButton(
                text = "Share it",
                onClick = {
                    val meta = state.statusVideos[pagerState.currentPage].meta
                    navigator.navigate(
                        StatusExportScreenDestination(
                            state.statusVideos[pagerState.currentPage].url,
                            state.userProfile!!,
                            meta?.h ?: 1280,
                            meta?.w ?: 720,
                            overlayProperties[overlayStyleIndex],
                        )
                    )

//                    navigator.navigate(VideoSuccessScreenDestination())
                },
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                icon = Icons.Default.Share,
                textColor = Color.White,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Purple4,
                )
            )
        }
    }
}

@Composable
fun VideoVerticalPager(
    state: StatusMakerState, // Replace with your actual state type
    pagerState: PagerState,
    overlaySection: @Composable () -> Unit = {},
    socialStatsLayout: @Composable () -> Unit = {}
) {
    VerticalPager(
        count = state.statusVideos.size,
        state = pagerState,
        modifier = Modifier.fillMaxSize()
    ) { page ->

        val video = state.statusVideos[page]

        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            VideoBackground(
                modifier = Modifier.fillMaxSize(),
                video = video,
                overlaySection = overlaySection,
                socialStatsLayout = socialStatsLayout
            )
        }
    }
}

@Composable
fun VideoBackground(
    modifier: Modifier = Modifier,
    video: StatusDto,
    overlaySection: @Composable () -> Unit = {},
    socialStatsLayout: @Composable () -> Unit = {}
) {
    Column(
        modifier = modifier
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Horoscope1,
                        Horoscope2,
                        Horoscope3,
                    )
                )
            ),
        verticalArrangement = Arrangement.Center,
    ) {

        val videoAspectRatio = (video.meta?.a ?: 1.0f).toFloat()

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(videoAspectRatio),
            contentAlignment = Alignment.Center
        ) {
            ListVideoPlayer(
                videoUrl = video.url,
                modifier = Modifier.fillMaxWidth()
            )
            Box(modifier = Modifier.align(alignment = Alignment.Center)) {
                overlaySection()
            }
        }
        socialStatsLayout()
    }
}

@Composable
fun SocialStatsRow(
    modifier: Modifier = Modifier,
    socialStats: SocialStats,
    onPrevious: () -> Unit,
    onNext: () -> Unit
) {

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onPrevious,
            modifier = Modifier
                .size(48.dp)
                .background(Color.Black.copy(alpha = 0.4f), CircleShape)
                .border(1.dp, Color.White.copy(alpha = 0.1f), CircleShape)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = "Previous",
                tint = Color.White
            )
        }

        Card(
            modifier = modifier
                .fillMaxWidth()
                .weight(1f),
            colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.4f)),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(
                                Purple4.copy(alpha = 0.2f),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.ChatBubbleOutline,
                            contentDescription = "Views",
                            tint = Purple4,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Text(
                        text = "${socialStats.views / 1000}K",
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Medium
                        )
                    )
                }


                Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(
                                Color.Red.copy(alpha = 0.2f),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Favorite,
                            contentDescription = "Views",
                            tint = Color.Red,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Text(
                        text = "${socialStats.views / 1000}K",
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Medium
                        )
                    )
                }

                Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(
                                Purple4.copy(alpha = 0.2f),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.RemoveRedEye,
                            contentDescription = "Views",
                            tint = Purple4,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Text(
                        text = "${socialStats.views / 1000}K",
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }
        }

        IconButton(
            onClick = onNext,
            modifier = Modifier
                .size(48.dp)
                .background(Color.Black.copy(alpha = 0.4f), CircleShape)
                .border(1.dp, Color.White.copy(alpha = 0.1f), CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "Next",
                tint = Color.White
            )
        }

    }

}


@Preview
@Composable
fun StatsPreview(modifier: Modifier = Modifier) {
    SocialStatsRow(
        modifier = modifier.fillMaxWidth(),
        socialStats = SocialStats(whatsappShares = 150, likes = 300, views = 5000),
        onPrevious = {},
        onNext = {}
    )
}

@Composable
fun EditProfileDialog(
    userProfile: UserProfile,
    onDismiss: () -> Unit,
    onSave: (UserProfile) -> Unit
) {
    var name by remember { mutableStateOf(userProfile.name) }
    var businessName by remember { mutableStateOf(userProfile.businessName) }
    var address by remember { mutableStateOf(userProfile.address) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Edit Profile")
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = businessName,
                    onValueChange = { businessName = it },
                    label = { Text("Business Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Address") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onSave(UserProfile(name, businessName, address, userProfile.profileImage))
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Preview
@Composable
fun TopOverlaySectionWithDiamondsPreview(modifier: Modifier = Modifier) {
    TopOverlaySection(
        modifier = modifier,
        title = "Made with Status Maker",
        accentColor = Purple4
    )
}

@Composable
fun TopOverlaySection(
    modifier: Modifier = Modifier,
    title: String = "Made with Sanatan Dharma",
    accentColor: Color = Purple4
) {
    return
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Black)
                .padding(vertical = 20.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .width(20.dp)
                    .height(2.dp)
                    .background(accentColor, RoundedCornerShape(1.dp))
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Normal,
                    color = Color.White,
                    letterSpacing = 1.2.sp,
                    fontSize = 11.sp
                )
            )

            Spacer(modifier = Modifier.width(12.dp))

            Box(
                modifier = Modifier
                    .width(20.dp)
                    .height(2.dp)
                    .background(accentColor, RoundedCornerShape(1.dp))
            )
        }
    }
}