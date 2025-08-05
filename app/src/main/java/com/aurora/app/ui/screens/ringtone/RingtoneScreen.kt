package com.aurora.app.ui.screens.ringtone

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.aurora.app.R
import com.aurora.app.domain.model.dashboard.Ringtone
import com.aurora.app.ui.components.AuroraTopBar
import com.aurora.app.ui.navigation.ScreenTransition
import com.aurora.app.utils.showToast
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@OptIn(ExperimentalMaterial3Api::class)
@Destination<RootGraph>(style = ScreenTransition::class)
@Composable
fun RingtoneScreen(
    navigator: DestinationsNavigator,
    viewModel: RingtoneViewModel = hiltViewModel()
) {
    val ringtones by viewModel.ringtones.collectAsState()
    var selectedRingtone by remember { mutableStateOf<Ringtone?>(null) }
    val showSheet = remember { mutableStateOf(false) }
    val context = LocalContext.current
    rememberCoroutineScope()
    var showSuccessDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            AuroraTopBar(
                text = "Select Ringtone",
                navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
                onNavigationClick = { navigator.navigateUp() },
            )
        }
    ) { paddingValues ->

        Box {
            Image(
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(0.7f),
                painter = painterResource(R.drawable.bg_main),
                contentDescription = null,
                contentScale = ContentScale.Crop
            )

            RingtoneListSection(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxWidth(),
                ringtones = ringtones,
                onItemClick = { ringtone ->
                    selectedRingtone = ringtone
                    showSheet.value = true
                }
            )

            // Bottom sheet for selection
            if (showSheet.value && selectedRingtone != null) {
                RingtoneSelectionBottomSheet(
                    ringtone = selectedRingtone!!,
                    onDismiss = {
                        selectedRingtone = null
                        showSheet.value = false
                    },
                    onSuccess = {
                        showSheet.value = false
                        context.showToast("Ringtone set successfully!")
                    },
                )
            }

            // Success dialog
            if (showSuccessDialog) {
                AlertDialog(
                    onDismissRequest = { showSuccessDialog = false },
                    title = { Text("Ringtone Set") },
                    text = { Text("Your ringtone has been successfully updated.") },
                    confirmButton = {
                        Button(onClick = { showSuccessDialog = false }) {
                            Text("OK")
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun RingtoneListSection(
    modifier: Modifier = Modifier,
    ringtones: List<Ringtone>,
    onItemClick: (Ringtone) -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    var startAnimation by remember { mutableStateOf(false) }

    // Trigger when screen is actually visible
    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            startAnimation = true
        }
    }

    LazyColumn(
        modifier = modifier.fillMaxWidth()
    ) {
        itemsIndexed(ringtones) { index, ringtone ->
            val offsetX by animateDpAsState(
                targetValue = if (startAnimation) 0.dp else 300.dp,
                animationSpec = tween(
                    durationMillis = 400,
                    delayMillis = index * 60,
                    easing = FastOutSlowInEasing
                ),
                label = "SlideIn"
            )
            val alpha by animateFloatAsState(
                targetValue = if (startAnimation) 1f else 0f,
                animationSpec = tween(durationMillis = 400, delayMillis = index * 60),
                label = "FadeIn"
            )

            RingtoneItemView(
                title = ringtone.dname,
                onClick = { onItemClick(ringtone) },
                modifier = Modifier
                    .offset { IntOffset(x = offsetX.roundToPx(), y = 0) }
                    .alpha(alpha)
            )
        }
    }
}

@Composable
fun RingtoneItemView(
    modifier: Modifier = Modifier,
    title: String,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()

    // Bounce animation
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.97f else 1f,
        animationSpec = spring(
            stiffness = Spring.StiffnessMedium,
            dampingRatio = Spring.DampingRatioMediumBouncy
        ),
        label = "ScaleAnim"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(
                    bounded = true,
                    color = MaterialTheme.colorScheme.primary
                )
            ) { onClick() },
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.MusicNote,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
