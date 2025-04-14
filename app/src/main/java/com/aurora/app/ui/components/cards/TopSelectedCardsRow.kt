package com.aurora.app.ui.components.cards

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.aurora.app.ui.screens.tarotSelect.SelectableTarotCard


@Composable
fun TopSelectedCardsRow(
    modifier: Modifier = Modifier,
    spreadCount: Int,
    selectedCards: List<SelectableTarotCard>,
    isRevealed: Boolean,
    onClick: (SelectableTarotCard) -> Unit
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 24.dp)
    ) {
        val maxCardWidth = (maxWidth - 40.dp) / 5  // Small padding included
        val cardHeight = maxCardWidth * (3f / 2f)  // Maintain 2:3 aspect ratio

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            repeat(spreadCount) { index ->
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
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { onClick(card) },
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
        }
    }
}