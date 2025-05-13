package com.aurora.app.ui.screens.tarotSelect

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aurora.app.domain.model.spread.SpreadDetail
import com.aurora.app.ui.components.AuroraTopBar
import com.aurora.app.ui.components.button.AuroraButton
import com.aurora.app.ui.components.cards.BottomCardCardDeck
import com.aurora.app.ui.components.cards.cardselection.TopSelectedCardsView
import com.aurora.app.ui.screens.destinations.CardDetailScreenDestination
import com.aurora.app.ui.screens.destinations.SpreadResultScreenDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class)
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
        topBar = {
            AuroraTopBar(
                text = spreadDetail.title,
                navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
                onNavigationClick = { navigator.navigateUp() }
            )
        },
        content = { paddingValues ->

            Box(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    AnimatedVisibility(
                        visible = uiState.maxSelectedCards != 0, // or your own condition
                        enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
                        exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut()
                    ) {
                        TopSelectedCardsView(
                            modifier = Modifier,
                            spreadCount = uiState.maxSelectedCards,
                            selectedCards = selectedCards,
                            isRevealed = isRevealed,
                            onClick = { card ->
                                if (isRevealed) {
                                    val tarotCard = uiState.cards.find { it.id == card.cardId }
                                    tarotCard?.let {
                                        navigator.navigate(CardDetailScreenDestination(it))
                                    }
                                } else {
                                    Timber.e("Reveal is pending")
                                }
                            }
                        )
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.Bottom
                    ) {

                        AnimatedVisibility(
                            visible = selectedCards.size != uiState.maxSelectedCards,
                            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
                        ) {
                            BottomCardCardDeck(
                                cards = uiState.selectableCards,
                                selectedCards = selectedCards,
                                onCardSelected = { selected ->
                                    if (selectedCards.size < uiState.maxSelectedCards && !selectedCards.contains(selected)) {
                                        viewModel.addSelectedCard(selected)
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                maxSelectedCards = uiState.maxSelectedCards
                            )
                        }

                        AnimatedVisibility(
                            visible = selectedCards.size == uiState.maxSelectedCards && !uiState.isLoading,
                            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                if (uiState.isRevealed) {
                                    AuroraButton(
                                        text = "Get Result",
                                        onClick = {
                                            navigator.popBackStack()
                                            navigator.navigate(
                                                SpreadResultScreenDestination(spreadDetail)
                                            )
                                        }
                                    )
                                } else {
                                    AuroraButton(
                                        text = "Reveal Cards",
                                        onClick = { viewModel.setRevealed(true) },
                                    )
                                }
                            }
                        }
                    }
                }
            }

        }
    )

}
