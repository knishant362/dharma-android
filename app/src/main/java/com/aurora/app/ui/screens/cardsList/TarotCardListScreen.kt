package com.aurora.app.ui.screens.cardsList

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aurora.app.R
import com.aurora.app.domain.model.TarotCard
import com.aurora.app.ui.components.AuroraTopBar
import com.aurora.app.ui.components.BottomBar
import com.aurora.app.ui.screens.destinations.DashboardScreenDestination
import com.aurora.app.ui.screens.destinations.TarotCardListScreenDestination
import com.aurora.app.utils.AssetImageLoader
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@OptIn(ExperimentalMaterial3Api::class)
@Destination
@Composable
fun TarotCardListScreen(
    navigator: DestinationsNavigator,
    viewModel: TarotViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val filters by viewModel.filters.collectAsState()
    var selectedFilter by remember { mutableStateOf("All") }
    var isGrid by remember { mutableStateOf(true) }

    Scaffold(
        modifier = Modifier.background(color = Color.Transparent),
        topBar = {
            AuroraTopBar(titleRes = R.string.app_name)
        },
        content = { paddingValues ->

            Box(
                modifier = Modifier
                    .padding(paddingValues)
            ) {

                Column{

                    TarotFilterChips(
                        filters = filters,
                        selected = selectedFilter,
                        onFilterSelected = { selectedFilter = it }
                    )

                    Button(
                        onClick = { isGrid = !isGrid },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text(if (isGrid) "Switch to List" else "Switch to Grid")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

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
                            val filteredCards = when (selectedFilter) {
                                "All" -> allCards
                                in allCards.map { it.type } -> allCards.filter { it.type == selectedFilter }
                                in allCards.mapNotNull { it.suit } -> allCards.filter { it.suit == selectedFilter }
                                else -> allCards
                            }

                            if (isGrid) {
                                LazyVerticalGrid(
                                    columns = GridCells.Fixed(2),
                                    contentPadding = PaddingValues(4.dp),
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    items(filteredCards.size) { index ->
                                        TarotCardItem(card = filteredCards[index])
                                    }
                                }
                            } else {
                                LazyColumn(modifier = Modifier.fillMaxSize()) {
                                    items(filteredCards.size) { index ->
                                        TarotCardItem(card = filteredCards[index])
                                    }
                                }
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
    filters: List<String>,
    selected: String,
    onFilterSelected: (String) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        items(filters) { filter ->
            val isSelected = selected == filter
            FilterChip(
                selected = isSelected,
                onClick = { onFilterSelected(filter) },
                label = { Text(filter) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.onBackground,
                    selectedLabelColor = MaterialTheme.colorScheme.background
                )
            )
        }
    }
}

@Composable
fun TarotCardItem(card: TarotCard) {
    val context = LocalContext.current
    val bitmap by remember(card.imageRes) {
        mutableStateOf(AssetImageLoader.loadBitmapFromAsset(context, card))
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clip(RoundedCornerShape(4.dp))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                shape = RoundedCornerShape(4.dp)
            )
            .shadow(elevation = 6.dp, shape = RoundedCornerShape(1.dp))
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
