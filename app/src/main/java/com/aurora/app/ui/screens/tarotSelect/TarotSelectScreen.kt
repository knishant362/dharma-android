package com.aurora.app.ui.screens.tarotSelect

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.aurora.app.domain.model.SelectableCard
import com.aurora.app.ui.components.TarotCardGameScreen
import com.ramcosta.composedestinations.annotation.Destination

@Destination
@Composable
fun TarotSelectScreen(modifier: Modifier = Modifier) {
    // Sample 24-card deck using tarot back image
    var cards by remember {
        mutableStateOf(
            List(24) { index ->
                SelectableCard(id = index, imageRes = "card_back") // replace with your real asset name
            }
        )
    }

    // Filter only selected cards
    val selectedCards = cards.filter { it.isSelected }

    TarotCardGameScreen()

}
