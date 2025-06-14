package com.aurora.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.aurora.app.ui.components.button.AuroraButton
import kotlinx.coroutines.delay

@Composable
fun ResultWaitingView(
    modifier: Modifier = Modifier,
    waitTimeInSeconds: Int,
    loadingTimeThreshold: Int,
    onReadNow: () -> Unit,
    onReadResult: () -> Unit
) {
    var timeLeft by remember { mutableStateOf(waitTimeInSeconds) }
    val progress = (waitTimeInSeconds - timeLeft).toFloat() / waitTimeInSeconds
    val isCompleted = timeLeft == 0

    LaunchedEffect(Unit) {
        while (timeLeft > 0) {
            delay(1000)
            timeLeft--
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = if (isCompleted) "Your love reading is ready.\nDiscover the mysteries it reveals."
            else "Your reading is being prepared",
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )

        if (!isCompleted) {
            LinearProgressWithIcon(progress = progress)
            Text(
                text = "${timeLeft}s remaining",
                style = MaterialTheme.typography.bodyMedium
            )
        }

        if (isCompleted) {
            AuroraButton(
                text = "Reveal Cards",
                onClick = onReadResult,
                icon = Icons.Default.PlayArrow
            )
        } else if (timeLeft < loadingTimeThreshold) {
            AuroraButton(
                text = "Loading",
                onClick = onReadNow,
                icon = Icons.Default.PlayArrow,
                enabled = false
            )
        } else {
            AuroraButton(
                text = "Read Now (Ads)",
                onClick = onReadNow,
                icon = Icons.Default.PlayArrow,
            )
        }

    }
}