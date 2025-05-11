package com.aurora.app.ui.components.cards

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.text.font.FontWeight
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
    val rotationOffset = remember { mutableFloatStateOf(0f) }
    val draggableState = rememberDraggableState { delta -> rotationOffset.floatValue += delta / 6f }
    val isDraggable = selectedCards.size < maxSelectedCards

    Box(
        modifier = modifier
            .fillMaxWidth()
//            .background(Color.Gray) // Size Debugging
            .draggable(
                state = draggableState,
                orientation = Orientation.Horizontal,
                enabled = isDraggable,
                onDragStarted = { startOffset ->
                    // Optional: Handle drag start (e.g., log or reset state)
                    println("Drag started at offset: $startOffset")
                },
                onDragStopped = { velocity ->
                    // Optional: Handle drag stop (e.g., apply fling or reset)
                    println("Drag stopped with velocity: $velocity")
                }
            ),
        contentAlignment = Alignment.TopCenter
    ) {
        FanLayout(
            modifier = Modifier,
            cards = cards,
            rotationOffset = rotationOffset.floatValue,
            selectedCards = selectedCards,
            onCardSelected = onCardSelected
        )

        Text(
            text = "<--- Select the card ---->",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier.align(Alignment.BottomCenter)
                .padding(16.dp)
        )
    }
}

@Composable
fun FanLayout(
    modifier: Modifier = Modifier,
    cards: List<SelectableTarotCard>,
    rotationOffset: Float,
    selectedCards: List<SelectableTarotCard>,
    onCardSelected: (SelectableTarotCard) -> Unit
) {
    val arcAngle = 180f
    val cardAngle = if (cards.size > 1) arcAngle / (cards.size - 1) else 0f
    val radius = 1800f

    Layout(
        modifier = modifier.offset(y = (radius/3).dp),
        content = {
            cards.forEach { card ->
                val isSelected = selectedCards.any { it.id == card.id }
                TarotCardItem(
                    card = card,
                    isSelected = isSelected,
                    onClick = { onCardSelected(card) }
                )
            }
        }
    ) { measurables, constraints ->
        val placeables = measurables.map { it.measure(constraints) }
        val width = constraints.maxWidth
        val height = constraints.maxHeight

        layout(width, height) {
            placeables.forEachIndexed { index, placeable ->
                val angle = -45f + index * cardAngle + rotationOffset
                val rad = Math.toRadians(angle.toDouble())
                val x = (width / 2) + sin(rad) * radius - placeable.width / 2
                val y = height - cos(rad) * radius - placeable.height

                placeable.placeWithLayer(
                    x = x.toInt(),
                    y = y.toInt(),
                    layerBlock = {
                        rotationZ = angle
                        transformOrigin = TransformOrigin(0.5f, 1f)
                    }
                )
            }
        }
    }
}