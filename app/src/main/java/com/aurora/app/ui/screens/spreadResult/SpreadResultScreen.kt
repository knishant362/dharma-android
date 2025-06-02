package com.aurora.app.ui.screens.spreadResult

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import com.aurora.app.domain.model.TarotCard
import com.aurora.app.domain.model.spread.SpreadDetail
import com.aurora.app.ui.components.AuroraTopBar
import com.aurora.app.ui.components.OnLifecycleEvent
import com.aurora.app.ui.components.button.AuroraButton
import com.aurora.app.ui.screens.cardDetail.CardDetailImage
import com.aurora.app.utils.showToast
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.CardDetailScreenDestination
import com.ramcosta.composedestinations.generated.destinations.TarotSelectScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.flow.collectLatest
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class)
@Destination<RootGraph>
@Composable
fun SpreadResultScreen(
    spreadDetail: SpreadDetail,
    navigator: DestinationsNavigator,
    viewModel: SpreadResultViewModel = hiltViewModel()
) {

    OnLifecycleEvent(Lifecycle.Event.ON_CREATE) {
        Timber.e("SpreadResultScreen : ON_CREATE")
        viewModel.setupResult(spreadDetail.id)
    }

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is SpreadResultUiEvent.NavigateToDrawScreen -> {
                    navigator.popBackStack()
                    navigator.navigate(TarotSelectScreenDestination(spreadDetail))
                }
                is SpreadResultUiEvent.ShowError -> {
                    context.showToast(event.message)
                }
            }
        }
    }

    val uiState by viewModel.uiState.collectAsState()
    val cards = uiState.tarotCards

    Scaffold(
        topBar = {
            AuroraTopBar(
                text = uiState.time,
                navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
                onNavigationClick = { navigator.navigateUp() })
        },
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .padding(paddingValues)
            ) {
                var currentCardIndex by remember { mutableIntStateOf(0) }

                if (cards.isNotEmpty()) {
                    Box {
                        DailySpreadScreen(
                            tarotCards = cards,
                            currentCardIndex = currentCardIndex,
                            onPrevCard = {
                                if (currentCardIndex > 0) {
                                    currentCardIndex--
                                }
                            },
                            onNextCard = {
                                if (currentCardIndex < cards.size - 1) {
                                    currentCardIndex++
                                }
                            },
                            onCardClick = { card ->
                                navigator.navigate(CardDetailScreenDestination(card))
                            },
                            modifier = Modifier
                        )
                        AuroraButton(
                            text = "Draw Again!",
                            onClick = { viewModel.onDrawAgainPressed() },
                            modifier = Modifier
                                .background(color = MaterialTheme.colorScheme.background)
                                .align(alignment = Alignment.BottomCenter)
                                .padding(horizontal = 16.dp),
                        )
                    }
                } else {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        CircularProgressIndicator()
                    }
                }

                if (uiState.errorMessage != null) {
                    Text(
                        text = uiState.errorMessage!!,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.White,
                            textAlign = TextAlign.Center
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 32.dp)
                    )
                }
            }
        },
    )

}

@Composable
fun DailySpreadScreen(
    modifier: Modifier = Modifier,
    tarotCards: List<TarotCard>,
    currentCardIndex: Int,
    onPrevCard: () -> Unit,
    onNextCard: () -> Unit,
    onCardClick: (TarotCard) -> Unit = {}
) {
    LazyColumn(
        modifier = modifier

    ) {
        item {
            Text(
                text = "♥ Tap the card for its meaning.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.White,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )

            Text(
                text = tarotCards[currentCardIndex].name,
                style = MaterialTheme.typography.titleLarge.copy(
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            )

            CardCarousel(
                cardImages = tarotCards,
                currentIndex = currentCardIndex,
                onPrev = onPrevCard,
                onNext = onNextCard,
                card = { card ->
                    CardDetailImage(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onCardClick(card) },
                        card.imagePath,
                        card.name
                    )
                }
            )
            Spacer(modifier = Modifier.height(24.dp))
            QuoteBox(
                title = "✦ SHIFT YOUR THINKING ✦",
                subTitle = tarotCards[currentCardIndex].description
            )
            Spacer(modifier = Modifier.height(68.dp))
        }
    }
}

@Composable
fun CardCarousel(
    cardImages: List<TarotCard>,
    currentIndex: Int,
    card: @Composable (TarotCard) -> Unit,
    onPrev: () -> Unit,
    onNext: () -> Unit
) {
    Column {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            IconButton(onClick = { onPrev() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous")
            }

            Spacer(modifier = Modifier.width(8.dp))

            Box(
                modifier = Modifier
                    .height(320.dp)
                    .aspectRatio(0.6f) // Approx card shape
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White)
                    .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp))
            ) {
                card(cardImages[currentIndex])
            }

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(onClick = { onNext() }) {
                Icon(Icons.Default.ArrowForward, contentDescription = "Next")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "CARD ${currentIndex + 1} / ${cardImages.size}",
            style = MaterialTheme.typography.labelMedium.copy(
                color = Color.Gray
            ),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }

}

@Composable
fun QuoteBox(title: String, subTitle: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium.copy(
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = subTitle,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.fillMaxWidth()
        )
    }
}