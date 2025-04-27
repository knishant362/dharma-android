package com.aurora.app.ui.screens.tarotSelect

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aurora.app.domain.model.spread.SpreadDetail
import com.aurora.app.ui.components.cards.BottomCardCardDeck
import com.aurora.app.ui.components.cards.TopSelectedCardsRow
import com.aurora.app.ui.screens.destinations.CardDetailScreenDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import timber.log.Timber

@Destination
@Composable
fun TarotSelectScreen(
    navigator: DestinationsNavigator,
    spreadDetail: SpreadDetail,
    viewModel: TarotSelectViewModel = hiltViewModel()
) {

    LaunchedEffect(Unit) {
        viewModel.setupSpread(spreadDetail)
    }

    val uiState by viewModel.uiState.collectAsState()

    val selectedCards = uiState.selectedCards
    val isRevealed = uiState.isRevealed

    Timber.e("TarotSelectScreen: ${uiState.selectableCards.size}")

    Scaffold(
        topBar = {},
        content = { paddingValues ->

            Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {

                TopSelectedCardsRow(
                    spreadCount = uiState.maxSelectedCards,
                    selectedCards = selectedCards,
                    isRevealed = isRevealed,
                    onClick = { card ->
                        if (isRevealed){
                            val tarotCard = uiState.cards.find { it.id == card.cardId }
                            tarotCard?.let {
                                navigator.navigate(CardDetailScreenDestination(it))
                            }
                        } else {
                            Timber.e("Reveal is pending")
                        }
                    }
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                ) {
                    BottomCardCardDeck(
                        cards = uiState.selectableCards,
                        selectedCards = selectedCards,
                        onCardSelected = { selected ->
                            if (selectedCards.size < uiState.maxSelectedCards && !selectedCards.contains(selected)) {
                                viewModel.addSelectedCard(selected)
                            }
                        },
                        modifier = Modifier.align(Alignment.BottomCenter),
                        maxSelectedCards = uiState.maxSelectedCards
                    )
                }

                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(onClick = {
                        viewModel.clearSelectedCards()
                    }) {
                        Text("Reset Deck")
                    }

                    Button(
                        onClick = { viewModel.setRevealed(true) },
                        enabled = selectedCards.size == uiState.maxSelectedCards
                    ) {
                        Text("Reveal Cards")
                    }
                }
            }

        }
    )

}
