package com.aurora.app.ui.screens.cardsList

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter.Companion.tint
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aurora.app.R
import com.aurora.app.domain.model.TarotCard
import com.aurora.app.domain.model.spread.FilterItem
import com.aurora.app.ui.components.AuroraTopBar
import com.aurora.app.ui.components.BottomBar
import com.aurora.app.utils.AssetImageLoader
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.CardDetailScreenDestination
import com.ramcosta.composedestinations.generated.destinations.TarotCardListScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class)
@Destination<RootGraph>
@Composable
fun TarotCardListScreen(
    navigator: DestinationsNavigator,
    viewModel: TarotViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val filters by viewModel.filters.collectAsState()
    var selectedFilter by remember { mutableStateOf<FilterItem?>(null) }

    LaunchedEffect(filters) {
        if (filters.isNotEmpty() && selectedFilter == null) {
            selectedFilter = filters.firstOrNull()
        }
    }

    Timber.e("selected: filters: ${filters.size} ,selected: $selectedFilter")


    Scaffold(
        modifier = Modifier.background(color = Color.Transparent),
        topBar = {
            AuroraTopBar(titleRes = R.string.app_name)
        },
        content = { paddingValues ->
            Column (
                modifier = Modifier
                    .padding(paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally
            ){

                TarotFilterChips(
                    filters = filters,
                    selected = selectedFilter,
                    onFilterSelected = { selectedFilter = it }
                )

                when (state) {
                    is TarotUiState.Loading -> Box(
                        Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }

                    is TarotUiState.Error -> Box(
                        Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Error: ${(state as TarotUiState.Error).message}")
                    }

                    is TarotUiState.Success -> {
                        val allCards = (state as TarotUiState.Success).cards
                        val filteredCards = when {
                            filters.indexOf(selectedFilter) == 0 -> allCards
                            else -> allCards.filter { it.type == selectedFilter?.title }
                        }

                        LazyVerticalGrid(
                            columns = GridCells.Fixed(3),
                            contentPadding = PaddingValues(4.dp),
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp)
                        ) {

                            selectedFilter?.let { filter ->
                                item(span = { GridItemSpan(maxLineSpan) }) {
                                    FilterDescription(filter = filter)
                                }
                            }

                            items(filteredCards.size) { index ->
                                TarotCardItem(
                                    card = filteredCards[index],
                                    onClick = { card -> navigator.navigate(
                                        CardDetailScreenDestination(card)
                                    ) }
                                )
                            }
                        }

                    }
                }
            }

        },
        bottomBar = {
            BottomBar(navigator, TarotCardListScreenDestination.route)
        }
    )
}

@Composable
fun TarotFilterChips(
    modifier: Modifier = Modifier,
    filters: List<FilterItem>,
    selected: FilterItem?,
    onFilterSelected: (FilterItem) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
            .padding(horizontal = 8.dp, vertical = 16.dp)
    ) {
        items(filters) { filter ->

            val context = LocalContext.current
            val bitmap by remember(filter.id) {
                mutableStateOf(AssetImageLoader.loadBitmapFromAsset(context, filter.imagePath))
            }

            val isSelected = selected?.title == filter.title
            val borderColor = MaterialTheme.colorScheme.primary
            val backgroundColor = if (isSelected) borderColor else Color.Transparent
            val iconTint = if (isSelected) MaterialTheme.colorScheme.onPrimary else borderColor

            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(backgroundColor)
                    .border(
                        width = if (isSelected) 0.dp else 1.dp,
                        color = borderColor,
                        shape = CircleShape
                    )
                    .clickable { onFilterSelected(filter) },
                contentAlignment = Alignment.Center
            ) {
                bitmap?.let {
                    Image(
                        bitmap = it,
                        contentDescription = filter.title,
                        modifier = Modifier.size(38.dp),
                        contentScale = ContentScale.FillWidth,
                        colorFilter = tint(iconTint)
                    )
                }
            }
        }
    }
}

@Composable
fun FilterDescription(filter: FilterItem, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .padding(horizontal = 8.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier.padding(12.dp),
            text = filter.title.uppercase(),
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center
        )
        Text(
            modifier = Modifier.padding(12.dp),
            text = stringResource(if(filter.description == 0) R.string.tap_to_detail else filter.description),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
    }
}


@Composable
fun TarotCardItem(card: TarotCard, onClick: (TarotCard) -> Unit = {}) {
    val context = LocalContext.current
    val bitmap by remember(card.id) {
        mutableStateOf(AssetImageLoader.loadBitmapFromAsset(context, card.imagePath))
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(12.dp)
            )
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick.invoke(card) }
    ) {
        bitmap?.let {
            Image(
                bitmap = it,
                contentDescription = card.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(1.dp)),
                contentScale = ContentScale.FillWidth
            )
        }
    }
}
