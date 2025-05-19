package com.aurora.app.ui.screens.explore

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aurora.app.R
import com.aurora.app.domain.model.explore.ExploreItem
import com.aurora.app.ui.components.AuroraTopBar
import com.aurora.app.ui.components.modifierExtensions.radialGradientBackground
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@OptIn(ExperimentalMaterial3Api::class)
@Destination
@Composable
fun ExploreScreen(
    navigator: DestinationsNavigator,
    viewModel: ExploreViewModel = hiltViewModel()
) {

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

                ExploreSection(exploreItems = uiState.exploreItems)

            }
        }
    )
}

@Composable
fun ExploreSection(
    modifier: Modifier = Modifier,
    exploreItems: List<ExploreItem>
) {

    LazyColumn(modifier = modifier.padding(16.dp)) {
        items(exploreItems) { exploreItem ->
            ExploreItemView(exploreItem = exploreItem)
        }
    }
}

@Composable
fun ExploreItemView(
    modifier: Modifier = Modifier,
    exploreItem: ExploreItem
) {
    Box(
        modifier = modifier
            .padding(8.dp)
            .height(100.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .radialGradientBackground(),
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
                text = exploreItem.title,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Next",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
