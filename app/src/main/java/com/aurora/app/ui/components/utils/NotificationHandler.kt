package com.aurora.app.ui.components.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.onesignal.OneSignal
import kotlinx.coroutines.launch

@Composable
fun rememberNotificationPermissionRequester(
    context: Context = LocalContext.current,
    onPermissionResult: (Boolean) -> Unit
): () -> Unit {
    val coroutineScope = rememberCoroutineScope()
    var shouldRequestOneSignal by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            shouldRequestOneSignal = true
        } else {
            onPermissionResult(false)
        }
    }

    LaunchedEffect(shouldRequestOneSignal) {
        if (shouldRequestOneSignal) {
            OneSignal.Notifications.requestPermission(false)
            onPermissionResult(true)
            shouldRequestOneSignal = false
        }
    }

    val isGranted = remember {
        Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU || ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    }

    return remember {
        {
            if (isGranted) {
                coroutineScope.launch {
                    OneSignal.Notifications.requestPermission(false)
                    onPermissionResult(true)
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
    }
}
