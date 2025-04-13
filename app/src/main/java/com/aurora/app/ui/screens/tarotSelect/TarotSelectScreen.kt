package com.aurora.app.ui.screens.tarotSelect

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aurora.app.domain.model.spread.SpreadDetail
import com.aurora.app.ui.components.cards.BottomCardCardDeck
import com.aurora.app.ui.components.cards.TopSelectedCardsRow
import com.aurora.app.ui.screens.destinations.SpreadDetailScreenDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import timber.log.Timber

@Destination
@Composable
fun TarotSelectScreen(
    modifier: Modifier = Modifier,
    navigator: DestinationsNavigator,
    spreadDetail: SpreadDetail,
    viewModel: TarotSelectViewModel = hiltViewModel()
) {

    LaunchedEffect(Unit) {
        viewModel.setupSpread(spreadDetail)
    }

    val uiState by viewModel.uiState.collectAsState()
    val selectedCards = remember { mutableStateListOf<SelectableTarotCard>() }
    val isRevealed = remember { mutableStateOf(false) }

    Timber.e("TarotSelectScreen: ${uiState.selectableCards}")

    Box(modifier = Modifier.fillMaxSize()) {

        TopSelectedCardsRow(
            spreadCount = uiState.maxSelectedCards,
            selectedCards = selectedCards,
            isRevealed = isRevealed.value,
            onClick = { card ->
                if (isRevealed.value){
                    navigator.navigate(SpreadDetailScreenDestination(card))
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
                    if (selectedCards.size < 5 && !selectedCards.contains(selected)) {
                        selectedCards.add(selected)
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
                selectedCards.clear()
                isRevealed.value = false
            }) {
                Text("Reset Deck")
            }

            Button(
                onClick = { isRevealed.value = true },
                enabled = selectedCards.size == 5
            ) {
                Text("Reveal Cards")
            }
        }
    }

}
