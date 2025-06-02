package com.aurora.app.ui.screens.explore

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aurora.app.R
import com.aurora.app.domain.model.spread.SpreadDetailDTO
import com.aurora.app.domain.model.spread.toSpreadDetail
import com.aurora.app.ui.components.AuroraTopBar
import com.aurora.app.ui.components.OnLifecycleEvent
import com.aurora.app.ui.components.modifierExtensions.radialGradientBackground
import com.aurora.app.ui.navigation.ScreenTransition
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.SpreadResultScreenDestination
import com.ramcosta.composedestinations.generated.destinations.TarotSelectScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class)
@Destination<RootGraph>(style = ScreenTransition::class)
@Composable
fun ExploreScreen(
    navigator: DestinationsNavigator,
    viewModel: ExploreViewModel = hiltViewModel()
) {


    OnLifecycleEvent(Lifecycle.Event.ON_CREATE) {
        Timber.e("SpreadDetailScreen : ON_CREATE")
        viewModel.setupExploreData()
    }


    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            AuroraTopBar(
                titleRes = R.string.app_name,
                navigationIcon = Icons.AutoMirrored.Rounded.ArrowBack,
                onNavigationClick = {
                    navigator.navigateUp()
                }
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    text = uiState.title,
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.titleLarge.copy(textAlign = TextAlign.Center)
                )

                ExploreSection(
                    exploreItems = uiState.spreads,
                    onExploreItemClick = {
                        if (it.spreadResult == null) {
                            navigator.navigate(TarotSelectScreenDestination(it.toSpreadDetail()))
                        } else {
                            navigator.navigate(SpreadResultScreenDestination(it.toSpreadDetail()))
                        }
                    }
                )
            }
        }
    )
}

@Composable
fun ExploreSection(
    modifier: Modifier = Modifier,
    exploreItems: List<SpreadDetailDTO>,
    onExploreItemClick: (SpreadDetailDTO) -> Unit = {}
) {

    LazyColumn(modifier = modifier.padding(16.dp)) {
        items(exploreItems) { exploreItem ->
            ExploreItemView(spreadDetail = exploreItem, onExploreItemClick = onExploreItemClick)
        }
    }
}

@Composable
fun ExploreItemView(
    modifier: Modifier = Modifier,
    spreadDetail: SpreadDetailDTO,
    onExploreItemClick: (SpreadDetailDTO) -> Unit
) {

    val isResultExist = spreadDetail.spreadResult != null

    Box(
        modifier = modifier
            .padding(8.dp)
            .height(100.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .radialGradientBackground()
            .border(
                width = if (isResultExist) 2.dp else 0.dp,
                color = if (isResultExist) MaterialTheme.colorScheme.primary else Color.Transparent,
                shape = RoundedCornerShape(16.dp)
            ).clickable { onExploreItemClick(spreadDetail) },
        contentAlignment = Alignment.Center
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = spreadDetail.title,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = if (isResultExist) Icons.Rounded.CheckCircle else Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = if (isResultExist) "View Result" else "Next",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
