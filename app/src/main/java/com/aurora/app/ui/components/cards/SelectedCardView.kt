package com.aurora.app.ui.components.cards

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.aurora.app.ui.components.GradientBorderBox
import com.aurora.app.ui.screens.tarotSelect.SelectableTarotCard
import com.aurora.app.utils.AssetImageLoader

@Composable
fun SelectedCardView(
    modifier: Modifier = Modifier,
    card: SelectableTarotCard?,
    isRevealed: Boolean,
    onClick: (SelectableTarotCard) -> Unit
) {
    val context = LocalContext.current

    if (card == null) {
        GradientBorderBox(modifier = modifier) { /* App Logo here */ }
        return
    }

    val rotationY by animateFloatAsState(
        targetValue = if (isRevealed) 180f else 0f,
        animationSpec = tween(durationMillis = 600),
        label = "card_flip"
    )

    val isFrontVisible = rotationY > 90f

    val frontBitmap by remember(card.id) {
        mutableStateOf(AssetImageLoader.loadBitmapFromAsset(context, card.frontImage))
    }

    GradientBorderBox(
        modifier = modifier
            .graphicsLayer {
                this.rotationY = rotationY
                cameraDistance = 8 * density
            }
            .clickable { onClick(card) }
    ) {
        if (isFrontVisible) {
            frontBitmap?.let { bitmap ->
                Image(
                    bitmap = bitmap,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer { this.rotationY = 180f },
                    contentScale = ContentScale.Crop
                )
            }
        } else {
            Image(
                painter = painterResource(id = card.backImageRes),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
    }
}