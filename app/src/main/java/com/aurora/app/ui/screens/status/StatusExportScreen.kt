package com.aurora.app.ui.screens.status

import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aurora.app.R
import com.aurora.app.ui.components.AuroraTopBar
import com.aurora.app.ui.components.ProgressRingUI
import com.aurora.app.ui.navigation.ScreenTransition
import com.aurora.app.ui.screens.status.model.SharePlatform
import com.aurora.app.ui.screens.status.model.UserProfile
import com.aurora.app.ui.screens.status.tools.FixedVideoOverlayExporter
import com.aurora.app.ui.screens.status.tools.overlay.OverlayProperties
import com.aurora.app.utils.ShareUtility
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Destination<RootGraph>(style = ScreenTransition::class)
@Composable
fun StatusExportScreen(
    videoUrl: String,
    userProfile: UserProfile,
    height: Int,
    width: Int,
    overlayProperties: OverlayProperties,
    navigator: DestinationsNavigator
) {
    val context = LocalContext.current
    val activity = LocalActivity.current
    val scope = rememberCoroutineScope()

    var exportTime by remember { mutableStateOf(0L) }
    var exportStatus by remember { mutableStateOf("Ready to export") }
    var isExporting by remember { mutableStateOf(false) }
    var exportProgress by remember { mutableStateOf(0f) }
    var exportedVideoPath by remember { mutableStateOf<String?>(null) }

    val fixedExporter = remember { FixedVideoOverlayExporter(activity!!) }

    LaunchedEffect(Unit) {
        scope.launch {
            delay(1000)
            isExporting = true
            exportStatus = "Starting image overlay export..."
            exportProgress = 0f
            val startTime = System.currentTimeMillis()

            fixedExporter.exportVideoWithOverlayFast(
                videoUrl,
                userProfile,
                overlayProperties
            ) { progress ->
                exportProgress = progress.progress
                exportStatus = progress.status
            }.onSuccess { path ->
                exportTime = System.currentTimeMillis() - startTime
                exportedVideoPath = path
                exportStatus = "Image overlay export completed!"
                isExporting = false
            }.onFailure { error ->
                exportStatus = "Image overlay export failed: ${error.message}"
                isExporting = false
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {

        Image(
            painter = painterResource(id = R.drawable.bg_halo_5),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            AuroraTopBar(
                text = "Share your work",
                navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent),
                onNavigationClick = { navigator.navigateUp() }
            )

            Card(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                shape = RoundedCornerShape(20.dp),
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    ProgressRingUI(
                        icCompleted = exportedVideoPath != null,
                        progress = (exportProgress * 100f).coerceAtMost(100f),
                    )
                }
            }

            exportedVideoPath?.let { path ->
                ExportedInfoPanel(
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .fillMaxWidth(),
                    onShare = { platform ->
                        when (platform) {
                            SharePlatform.WhatsApp -> ShareUtility.shareToWhatsApp(context, path)
                            SharePlatform.Instagram -> ShareUtility.shareToInstagram(context, path)
                            SharePlatform.Facebook -> ShareUtility.shareToFacebook(context, path)
                            SharePlatform.More -> ShareUtility.shareToMore(context, path)
                        }
                    }
                )
            }
        }
    }

}


@Preview
@Composable
fun ExportInfoPanelPreview(modifier: Modifier = Modifier) {
    ExportedInfoPanel(
        modifier = modifier
            .background(color = Color.White),
        onShare = {}
    )
}

@Composable
fun ExportedInfoPanel(
    modifier: Modifier = Modifier,
    onShare: (SharePlatform) -> Unit = {},
) {

    Column(
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.animateContentSize()
        ) {
            Text(
                text = "Video Ready! 🎉",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Your masterpiece is complete",
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = { onShare(SharePlatform.WhatsApp) },
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
                    .shadow(8.dp, RoundedCornerShape(16.dp)),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent
                ),
                contentPadding = PaddingValues(0.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF009688),
                                    Color(0xFF4CAF50)
                                )
                            ),
                            RoundedCornerShape(16.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Upload,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Share to Whatsapp",
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = true,
            enter = slideInVertically() + fadeIn(),
            exit = slideOutVertically() + fadeOut()
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val shareOptions = listOf(
                            SharePlatform.WhatsApp,
                            SharePlatform.Instagram,
                            SharePlatform.Facebook,
                            SharePlatform.More
                        )

                        shareOptions.forEachIndexed { index, platform ->
                            Image(
                                painter = painterResource(id = platform.icon),
                                contentDescription = platform.name,
                                modifier = Modifier
                                    .size(48.dp)
                                    .clickable {
                                        onShare(platform)
                                    }
                            )
                        }
                    }
                }
            }
        }
    }
}