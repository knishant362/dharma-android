package com.aurora.app.ui.components.utils

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.aurora.app.BuildConfig
import com.aurora.app.R
import com.aurora.app.utils.AppNavigationHelper
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import timber.log.Timber


@Composable
fun ForceUpdateChecker(
    onExitApp: () -> Unit
) {
    val context = LocalContext.current
    val appNavigationHelper = remember { AppNavigationHelper(context) }
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val remoteConfig = Firebase.remoteConfig
        remoteConfig.fetchAndActivate()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val minSupported = remoteConfig.getString("min_supported_version")
                    Timber.d("ForceUpdateChecker - Min Supported: $minSupported")
                    if (isVersionOlder(BuildConfig.VERSION_NAME, minSupported)) {
                        showDialog = true
                    }
                }
            }
    }

    if (showDialog) {
        ForceUpdateDialog(
            onUpdateClick = {
                appNavigationHelper.openPlayStore()
            },
            onExitApp = onExitApp
        )
    }
}


@Composable
fun ForceUpdateDialog(
    onUpdateClick: () -> Unit,
    onExitApp: () -> Unit
) {
    Dialog(onDismissRequest = { /* Block dismiss */ }) {
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black)
                    .border(
                        border = BorderStroke(
                            width = 1.dp,
                            brush = Brush.linearGradient(
                                listOf(
                                    MaterialTheme.colorScheme.primary,
                                    Color.Black,
                                    Color.DarkGray,
                                    MaterialTheme.colorScheme.primary
                                )
                            )
                        ),
                        shape = RoundedCornerShape(28.dp)
                    )
                    .background(MaterialTheme.colorScheme.surface)
            ) {

                Image(
                    painter = painterResource(id = R.drawable.bg_halo_5), // 🔹 Your image
                    contentDescription = null,
                    modifier = Modifier
                        .matchParentSize()
                        .clip(RoundedCornerShape(28.dp)),
                    contentScale = ContentScale.Crop,
                    alpha = 0.3f
                )

                Box(
                    modifier = Modifier
                        .matchParentSize()
                )

                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom
                ) {

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Update Required",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "We’ve made important improvements. Please update to continue using the app.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White.copy(alpha = 0.9f),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = onUpdateClick,
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text("Update Now", style = MaterialTheme.typography.titleMedium)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    TextButton(onClick = onExitApp) {
                        Text(
                            "Exit App",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }
    }
}


private fun isVersionOlder(current: String, min: String): Boolean {
    return try {
        val currentParts = current.split(".").map { it.toIntOrNull() ?: 0 }
        val minParts = min.split(".").map { it.toIntOrNull() ?: 0 }

        for (i in 0 until maxOf(currentParts.size, minParts.size)) {
            val c = currentParts.getOrElse(i) { 0 }
            val m = minParts.getOrElse(i) { 0 }
            if (c < m) return true
            if (c > m) return false
        }
        false
    } catch (e: Exception) {
        false
    }
}
