package com.aurora.app.ui.screens.spreadDetail

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import com.aurora.app.BuildConfig
import com.aurora.app.R
import com.aurora.app.domain.model.spread.SpreadDetail
import com.aurora.app.ui.components.AuroraTopBar
import com.aurora.app.ui.components.BottomBar
import com.aurora.app.ui.components.OnLifecycleEvent
import com.aurora.app.ui.screens.destinations.SpreadDetailScreenDestination
import com.aurora.app.ui.screens.destinations.SpreadHistoryScreenDestination
import com.aurora.app.ui.screens.destinations.SpreadResultScreenDestination
import com.aurora.app.ui.screens.destinations.TarotSelectScreenDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class)
@Destination
@Composable
fun SpreadDetailScreen(
    navigator: DestinationsNavigator,
    viewModel: SpreadViewModel = hiltViewModel()
) {

    OnLifecycleEvent(Lifecycle.Event.ON_CREATE) {
        Timber.e("SpreadDetailScreen : ON_CREATE")
        viewModel.loadSpreadDetails()
    }

    val uiState by viewModel.spreadUiState

    Scaffold(
        topBar = {
            if (BuildConfig.DEBUG) {
                AuroraTopBar(
                    titleRes = R.string.app_name,
                    actionIcon = Icons.Default.DateRange,
                    onActionClick = {
                        navigator.navigate(SpreadHistoryScreenDestination)
                    })
            } else {
                AuroraTopBar(titleRes = R.string.app_name)
            }
        },
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .padding(paddingValues)
            ) {

                when (uiState) {
                    is SpreadDetailUiState.Loading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    is SpreadDetailUiState.Error -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = (uiState as SpreadDetailUiState.Error).message)
                        }
                    }

                    is SpreadDetailUiState.Success -> {
                        val spreads = (uiState as SpreadDetailUiState.Success).spreads
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(spreads) { spread ->
                                SpreadCardItem(spread = spread, onClick = {
                                    val result =
                                        (uiState as SpreadDetailUiState.Success).spreadResults.find { it.spreadDetailId == spread.id }
                                    if (result == null) {
                                        navigator.navigate(TarotSelectScreenDestination(spread))
                                    } else {
                                        navigator.navigate(SpreadResultScreenDestination(spread))
                                    }
                                }
                                )
                            }
                        }
                    }
                }

            }
        },
        bottomBar = {
            BottomBar(navigator, SpreadDetailScreenDestination.route)
        }
    )

}

@Composable
fun SpreadCardItem(
    modifier: Modifier = Modifier,
    spread: SpreadDetail,
    onClick: (SpreadDetail) -> Unit
) {
    Card(
        modifier = modifier
            .padding(12.dp)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(8.dp)
            )
            .fillMaxWidth()
            .clickable { onClick(spread) },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(spread.icon),
                contentDescription = null,
                modifier = Modifier
                    .size(120.dp),
                contentScale = ContentScale.Crop
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(text = spread.title, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = "${spread.cards.size} Cards",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = spread.description,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
