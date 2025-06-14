package com.aurora.app.ui.components.cards.cardselection

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aurora.app.ui.components.cards.SelectedCardView
import com.aurora.app.ui.screens.tarotSelect.SelectableTarotCard

@Composable
fun NineCardsView(
    modifier: Modifier = Modifier,
    selectedCards: List<SelectableTarotCard>,
    isRevealed: Boolean,
    onClick: (SelectableTarotCard) -> Unit
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        val totalHorizontalSpacing = 8.dp * 2
        val totalVerticalSpacing = 8.dp * 2

        val availableWidth = maxWidth - totalHorizontalSpacing
        val availableHeight = maxHeight - totalVerticalSpacing

        // Calculate card width and height based on available space
        val maxCardWidth = availableWidth / 3
        val maxCardHeight = availableHeight / 3

        // Maintain 2:3 aspect ratio, so final width is the smaller of the two
        val cardWidth = minOf(maxCardWidth, maxCardHeight * (2f / 3f))
        val cardHeight = cardWidth * (3f / 2f)

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            repeat(3) { rowIndex ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier,
                ) {
                    repeat(3) { colIndex ->
                        val index = rowIndex * 3 + colIndex
                        SelectedCardView(
                            modifier = Modifier
                                .width(cardWidth)
                                .height(cardHeight),
                            selectedCards.getOrNull(index),
                            isRevealed,
                            onClick
                        )
                    }
                }
            }
        }
    }
}
