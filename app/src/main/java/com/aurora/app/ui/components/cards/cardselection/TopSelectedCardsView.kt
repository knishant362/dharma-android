package com.aurora.app.ui.components.cards.cardselection

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.aurora.app.ui.screens.tarotSelect.SelectableTarotCard

@Composable
fun TopSelectedCardsView(
    modifier: Modifier = Modifier,
    spreadCount: Int,
    selectedCards: List<SelectableTarotCard>,
    isRevealed: Boolean,
    onClick: (SelectableTarotCard) -> Unit
) {
    when (spreadCount) {
        4 -> FourSelectedCardsView(modifier, selectedCards, isRevealed, onClick)
        5 -> FiveSelectedCardsView(modifier, selectedCards, isRevealed, onClick)
        9 -> NineCardsView(modifier, selectedCards, isRevealed, onClick)
        else -> HorizontalSelectedCardsView(modifier, spreadCount, selectedCards, isRevealed, onClick)
    }
}