package com.aurora.app.ui.components.cards.cardselection

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aurora.app.ui.components.cards.SelectedCardView
import com.aurora.app.ui.screens.tarotSelect.SelectableTarotCard

@Composable
fun HorizontalSelectedCardsView(
    modifier: Modifier = Modifier,
    spreadCount: Int,
    selectedCards: List<SelectableTarotCard>,
    isRevealed: Boolean,
    onClick: (SelectableTarotCard) -> Unit) {

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp)
    ) {
        val maxCardWidth = (maxWidth - 40.dp) / 3  // Small padding included
        val cardHeight = maxCardWidth * (3f / 2f)  // Maintain 2:3 aspect ratio

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            repeat(spreadCount) { index ->
                SelectedCardView(
                    modifier = Modifier
                        .width(maxCardWidth)
                        .height(cardHeight),
                    selectedCards.getOrNull(index),
                    isRevealed,
                    onClick
                )
            }
        }
    }
}