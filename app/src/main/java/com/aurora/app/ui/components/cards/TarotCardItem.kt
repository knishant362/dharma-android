package com.aurora.app.ui.components.cards

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.aurora.app.ui.screens.tarotSelect.SelectableTarotCard

@Composable
fun TarotCardItem(
    modifier: Modifier = Modifier,
    card: SelectableTarotCard,
    isSelected: Boolean,
    onClick: () -> Unit
) {

    val animatedOffset by animateFloatAsState(
        targetValue = if (isSelected) -40f else 0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy),
        label = "cardOffset_${card.id}"
    )
    val density = LocalDensity.current

    Box(
        modifier = modifier
            .size(100.dp, 150.dp)
            .graphicsLayer {
                translationY = animatedOffset
                cameraDistance = 12 * density.density
            }
            .clickable(enabled = !isSelected) { onClick() }
            .border(
                width = 2.dp,
                color = if (isSelected) Color.Yellow else Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            )
    ) {
        Image(
            painter = painterResource(id = card.backImageRes),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(12.dp))
                .shadow(8.dp),
            contentScale = ContentScale.Crop
        )
    }
}

