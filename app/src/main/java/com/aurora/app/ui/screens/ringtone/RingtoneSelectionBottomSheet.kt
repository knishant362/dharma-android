package com.aurora.app.ui.screens.ringtone

import android.Manifest
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.aurora.app.domain.model.dashboard.Ringtone
import com.aurora.app.ui.components.button.AuroraButton
import com.aurora.app.ui.components.button.AuroraOutlinedButton
import com.aurora.app.utils.showToast
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RingtoneSelectionBottomSheet(
    onDismiss: () -> Unit,
    ringtone: Ringtone,
    onSuccess: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var downloadRingtone by remember { mutableStateOf<Ringtone?>(null) }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isDownloading by remember { mutableStateOf(false) }
    var downloadStatus by remember { mutableStateOf("") }

    // Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.all { it.value }) {
            // After permission, check WRITE_SETTINGS and proceed
            if (!Settings.System.canWrite(context)) {
                val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS).apply {
                    data = Uri.parse("package:${context.packageName}")
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
                downloadStatus = "Please grant 'Modify system settings' permission"
                isDownloading = false
            } else {
                coroutineScope.launch {
                    downloadAndSetRingtone(
                        context,
                        downloadRingtone!!,
                        downloadRingtone!!.url,
                        updateStatus = { status ->
                            downloadStatus = status
                            isDownloading = false
                        }, onSuccess = {
                            scope.launch {
                                sheetState.hide()
                                onSuccess()
                            }
                        }
                    )
                }
            }
        } else {
            downloadStatus = "Permissions denied"
            context.showToast("Required permissions denied")
            isDownloading = false
        }
    }

    ModalBottomSheet(
        onDismissRequest = {
            scope.launch {
                sheetState.hide()
                onDismiss()
            }
        },
        sheetState = sheetState,
        content = {
            if (!isDownloading) {
                RingtoneSelectionView(
                    onDismiss = {
                        scope.launch { sheetState.hide() }
                    },
                    ringtone = ringtone,
                    onApply = {
                        downloadRingtone = ringtone
                        isDownloading = true
                        downloadStatus = "Checking permissions..."

                        val permissions = buildList {
                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                                add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            }
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                add(Manifest.permission.READ_MEDIA_AUDIO)
                            }
                        }.toTypedArray()

                        if (permissions.isNotEmpty()) {
                            permissionLauncher.launch(permissions)
                        } else {
                            // No runtime permissions needed for this version; just check WRITE_SETTINGS
                            if (!Settings.System.canWrite(context)) {
                                val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS).apply {
                                    data = Uri.parse("package:${context.packageName}")
                                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                }
                                context.startActivity(intent)
                                context.showToast("Please grant 'Modify system settings' permission")
                                isDownloading = false
                            } else {
                                coroutineScope.launch {
                                    downloadAndSetRingtone(
                                        context,
                                        downloadRingtone!!,
                                        downloadRingtone!!.url,
                                        updateStatus = { status ->
                                            downloadStatus = status
                                            isDownloading = false
                                        },
                                        onSuccess = {
                                            scope.launch {
                                                sheetState.hide()
                                                onSuccess()
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                )
            } else {
                Spacer(modifier = Modifier.height(16.dp))
                Text(downloadStatus)
                if (isDownloading) {
                    Spacer(modifier = Modifier.height(16.dp))
                    CircularProgressIndicator()
                }
            }

        }
    )
}

@Composable
fun RingtoneSelectionView(
    ringtone: Ringtone,
    onApply: () -> Unit,
    onDismiss: () -> Unit
) {
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
    var isPlaying by remember { mutableStateOf(false) }
    var progress by remember { mutableStateOf(0f) }
    val scope = rememberCoroutineScope()

    DisposableEffect(ringtone.url) {
        mediaPlayer?.release()

        val player = MediaPlayer()
        player.setDataSource(ringtone.url)
        player.setOnPreparedListener {
            it.start()
            isPlaying = true
            // Track progress
            scope.launch {
                while (it.isPlaying) {
                    progress = (it.currentPosition.toFloat() / it.duration)
                    delay(200)
                }
            }
        }
        player.setOnCompletionListener {
            isPlaying = false
            progress = 1f
        }
        player.prepareAsync()
        mediaPlayer = player

        onDispose {
            player.stop()
            player.release()
            isPlaying = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Title
        Text(
            text = ringtone.dname,
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Preview & set this ringtone",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                .clickable {
                    if (isPlaying) {
                        mediaPlayer?.pause()
                        isPlaying = false
                    } else {
                        mediaPlayer?.start()
                        isPlaying = true
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = if (isPlaying) "Pause" else "Play",
                modifier = Modifier.size(42.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(Modifier.height(24.dp))

        Slider(
            value = progress,
            onValueChange = {},
            modifier = Modifier.fillMaxWidth(),
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.primary,
                activeTrackColor = MaterialTheme.colorScheme.primary,
                inactiveTrackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
            )
        )

        Spacer(Modifier.height(32.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            AuroraOutlinedButton(
                text = "Cancel",
                onClick = {
                    mediaPlayer?.stop()
                    mediaPlayer?.release()
                    mediaPlayer = null
                    onDismiss()
                },
                modifier = Modifier.weight(1f),
            )
            AuroraButton(
                text = "Apply",
                textColor = MaterialTheme.colorScheme.onPrimary,
                onClick = { onApply() },
                modifier = Modifier.weight(1f),
            )
        }
    }
}
