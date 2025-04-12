package com.aurora.app.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import kotlinx.coroutines.launch
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

            TarotCardItem(
                card = card,
                angle = angle,
                translationX = x.toFloat(),
                translationY = y.toFloat(),
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
    LocalDensity.current
    val cards = remember { mutableStateListOf<TarotCard>().apply { addAll(generateTarotCards()) } }
    val selectedCards = remember { mutableStateListOf<TarotCard>() }

    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            RotatableFanCardDeck(
                cards = cards,
                selectedCards = selectedCards,
                onCardSelected = { selected ->
                    if (!selectedCards.contains(selected) && selectedCards.size < 5) {
                        val index = cards.indexOfFirst { it.id == selected.id }
                        cards[index] = selected.copy(isFlipped = true)
                        selectedCards.add(cards[index])
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
                cards.replaceAll { it.copy(isFlipped = false) }
            }) {
                Text("Reset Deck")
            }

            Button(
                onClick = {
                    selectedCards.forEach { selected ->
                        val index = cards.indexOfFirst { it.id == selected.id }
                        cards[index] = selected.copy(isFlipped = true)
                    }
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
    onClick: () -> Unit
) {
    val rotationY = remember { Animatable(0f) }
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()

    LaunchedEffect(card.isFlipped) {
        val target = if (card.isFlipped) 180f else 0f
        scope.launch {
            rotationY.animateTo(target, animationSpec = tween(600))
        }
    }

    val showFront = rotationY.value > 90f
    val imageRes = if (showFront) card.frontImageRes else card.backImageRes

    Box(
        modifier = Modifier
            .graphicsLayer {
                this.translationX = translationX
                this.translationY = translationY
                this.rotationZ = angle
                this.rotationY = rotationY.value
                this.transformOrigin = TransformOrigin(0.5f, 1f)
                this.cameraDistance = 12 * density.density
            }
            .clickable { onClick() }
            .border(
                width = 2.dp,
                color = if (card.isFlipped) Color.Yellow else Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            )
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = null,
            modifier = Modifier
                .size(100.dp, 150.dp)
                .clip(RoundedCornerShape(12.dp))
                .shadow(8.dp),
            contentScale = ContentScale.Crop
        )
    }
}


