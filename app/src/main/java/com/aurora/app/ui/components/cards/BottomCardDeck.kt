package com.aurora.app.ui.components.cards

import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.aurora.app.ui.screens.tarotSelect.SelectableTarotCard
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun BottomCardCardDeck(
    modifier: Modifier = Modifier,
    cards: List<SelectableTarotCard>,
    maxSelectedCards: Int,
    selectedCards: List<SelectableTarotCard>,
    onCardSelected: (SelectableTarotCard) -> Unit
) {
    val baseRadius = 1200f
    val baseAngle = -70f
    val arcAngle = 140f
    val cardAngle = arcAngle / (cards.size - 1).coerceAtLeast(1)

    val rotationOffset = remember { mutableFloatStateOf(0f) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .offset(y = (250).dp)
            .fillMaxHeight()
            .clipToBounds()
            .pointerInput(selectedCards.size < maxSelectedCards) {
                if (selectedCards.size < maxSelectedCards) {
                    detectHorizontalDragGestures { _, dragAmount ->
                        rotationOffset.floatValue += dragAmount / 6f
                    }
                }
            },
        contentAlignment = Alignment.BottomCenter
    ) {
        cards.forEachIndexed { index, card ->
            val angle = baseAngle + index * cardAngle + rotationOffset.floatValue
            val rad = Math.toRadians(angle.toDouble())
            val x = sin(rad) * baseRadius
            val y = -cos(rad) * baseRadius
            val isSelected = selectedCards.any { it.id == card.id }

            TarotCardItem(
                card = card,
                angle = angle,
                translationX = x.toFloat(),
                translationY = y.toFloat(),
                isSelected = isSelected,
                onClick = { onCardSelected(card) }
            )
        }
    }
}