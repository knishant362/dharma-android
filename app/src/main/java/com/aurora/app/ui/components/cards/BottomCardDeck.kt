package com.aurora.app.ui.components.cards

import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aurora.app.ui.screens.tarotSelect.SelectableTarotCard

@Composable
fun BottomCardCardDeck(
    modifier: Modifier = Modifier,
    cards: List<SelectableTarotCard>,
    maxSelectedCards: Int,
    selectedCards: List<SelectableTarotCard>,
    onCardSelected: (SelectableTarotCard) -> Unit
) {
    val scrollState = rememberLazyListState()
    val dragOffset = remember { mutableFloatStateOf(0f) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HorizontalCardLayout(
            modifier = Modifier.fillMaxWidth(),
            cards = cards,
            scrollState = scrollState,
            dragOffset = dragOffset.floatValue,
            selectedCards = selectedCards,
            onCardSelected = onCardSelected
        )

        Text(
            text = "<--- Select your cards ---->",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            ),
            modifier = Modifier
                .padding(16.dp)
        )
    }
}

@Composable
fun HorizontalCardLayout(
    modifier: Modifier = Modifier,
    cards: List<SelectableTarotCard>,
    scrollState: LazyListState,
    dragOffset: Float,
    selectedCards: List<SelectableTarotCard>,
    onCardSelected: (SelectableTarotCard) -> Unit
) {
    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .offset(x = (-dragOffset).dp),
        state = scrollState,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        flingBehavior = rememberSnapFlingBehavior(lazyListState = scrollState)
    ) {
        items(
            items = cards,
            key = { card -> card.id } // Stable keys for efficient recomposition
        ) { card ->
            val isSelected by rememberUpdatedState(selectedCards.any { it.id == card.id })
            TarotCardItem(
                card = card,
                isSelected = isSelected,
                onClick = { onCardSelected(card) }
            )
        }
    }
}