package com.aurora.app.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.aurora.app.R
import kotlin.math.cos
import kotlin.math.sin


data class TarotCard(
    val id: Int,
    val frontImageRes: Int,
    val backImageRes: Int,
    var isFlipped: Boolean = false
)
@Composable
fun RotatableFanCardDeck(
    cards: List<TarotCard>,
    selectedCards: List<TarotCard>,
    onCardSelected: (TarotCard) -> Unit,
    modifier: Modifier = Modifier
) {
    val baseRadius = 800f
    val baseAngle = -70f
    val arcAngle = 140f
    val cardAngle = arcAngle / (cards.size - 1).coerceAtLeast(1)

    val rotationOffset = remember { mutableFloatStateOf(0f) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(600.dp)
            .clipToBounds()
            .pointerInput(selectedCards.size < 5) {
                if (selectedCards.size < 5) {
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

fun generateTarotCards(count: Int = 24): List<TarotCard> {
    return List(count) { index ->
        TarotCard(
            id = index,
            backImageRes = R.drawable.card_back,
            frontImageRes = R.drawable.card_front
        )
    }
}

@Composable
fun TarotCardGameScreen() {
    val cards = remember { mutableStateListOf<TarotCard>().apply { addAll(generateTarotCards()) } }
    val selectedCards = remember { mutableStateListOf<TarotCard>() }
    val isRevealed = remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {

        TopSelectedCardsRow(selectedCards = selectedCards, isRevealed = isRevealed.value)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            RotatableFanCardDeck(
                cards = cards,
                selectedCards = selectedCards,
                onCardSelected = { selected ->
                    if (selectedCards.size < 5 && !selectedCards.contains(selected)) {
                        selectedCards.add(selected)
                    }
                },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = {
                selectedCards.clear()
                isRevealed.value = false
            }) {
                Text("Reset Deck")
            }

            Button(
                onClick = {
                    isRevealed.value = true
                },
                enabled = selectedCards.size == 5
            ) {
                Text("Reveal Cards")
            }
        }
    }
}


@Composable
fun TarotCardItem(
    card: TarotCard,
    angle: Float,
    translationX: Float,
    translationY: Float,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val density = LocalDensity.current

    Box(
        modifier = Modifier
            .graphicsLayer {
                this.translationX = translationX
                this.translationY = translationY + if (isSelected) -40f else 0f
                this.rotationZ = angle
                this.transformOrigin = TransformOrigin(0.5f, 1f)
                this.cameraDistance = 12 * density.density
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
                .size(100.dp, 150.dp)
                .clip(RoundedCornerShape(12.dp))
                .shadow(8.dp),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
fun TopSelectedCardsRow(
    selectedCards: List<TarotCard>,
    isRevealed: Boolean
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp)
    ) {
        val maxCardWidth = (maxWidth - 40.dp) / 5  // Small padding included
        val cardHeight = maxCardWidth * (3f / 2f)  // Maintain 2:3 aspect ratio

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            repeat(5) { index ->
                val card = selectedCards.getOrNull(index)

                Box(
                    modifier = Modifier
                        .width(maxCardWidth)
                        .height(cardHeight)
                        .clip(RoundedCornerShape(12.dp))
                        .border(2.dp, Color.Gray, RoundedCornerShape(12.dp))
                ) {
                    card?.let {
                        val imageRes = if (isRevealed) card.frontImageRes else card.backImageRes
                        Image(
                            painter = painterResource(id = imageRes),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
        }
    }
}

