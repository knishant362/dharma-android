package com.aurora.app.ui.components.cards.cardselection

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
fun FourSelectedCardsView(
    modifier: Modifier = Modifier,
    selectedCards: List<SelectableTarotCard>,
    isRevealed: Boolean,
    onClick: (SelectableTarotCard) -> Unit
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        val maxCardWidth = (maxWidth - 40.dp) / 3  // Small padding included
        val cardHeight = maxCardWidth * (3f / 2f)  // Maintain 2:3 aspect ratio

        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            SelectedCardView(
                modifier = Modifier
                    .width(maxCardWidth)
                    .height(cardHeight)
                    .padding(end = 2.dp),
                selectedCards.getOrNull(0),
                isRevealed,
                onClick
            )

            Column(
                modifier = Modifier,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                SelectedCardView(
                    modifier = Modifier
                        .width(maxCardWidth)
                        .height(cardHeight)
                        .padding(2.dp),
                    selectedCards.getOrNull(1),
                    isRevealed,
                    onClick
                )

                SelectedCardView(
                    modifier = Modifier
                        .width(maxCardWidth)
                        .height(cardHeight)
                        .padding(2.dp),
                    selectedCards.getOrNull(2),
                    isRevealed,
                    onClick
                )
            }

            SelectedCardView(
                modifier = Modifier
                    .width(maxCardWidth)
                    .height(cardHeight)
                    .padding(start = 2.dp),
                selectedCards.getOrNull(3),
                isRevealed,
                onClick
            )
        }
    }
}