package com.aurora.app.ui.screens.spreadHistory

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aurora.app.data.model.SpreadResult
import com.aurora.app.ui.components.AuroraTopBar
import com.aurora.app.ui.navigation.ScreenTransition
import com.aurora.app.ui.screens.spreadList.SpreadDetailUiState
import com.aurora.app.ui.screens.spreadList.SpreadListViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.SpreadHistoryScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Destination<RootGraph>(style = ScreenTransition::class)
@Composable
fun SpreadHistoryScreen(
    navigator: DestinationsNavigator,
    viewModel: SpreadListViewModel = hiltViewModel()
) {
    val state by viewModel.spreadUiState
    var showToday by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadSpreadDetails()
    }

    Scaffold(
        topBar = {
            AuroraTopBar(
                text = "History",
                navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
                onActionClick = {
                    navigator.navigate(SpreadHistoryScreenDestination)
                })
        }
    ) { contentPadding ->
        Column(modifier = Modifier.padding(contentPadding)) {
            when (state) {
                is SpreadDetailUiState.Loading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                is SpreadDetailUiState.Error -> {
                    val message = (state as SpreadDetailUiState.Error).message
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Error: $message")
                    }
                }

                is SpreadDetailUiState.Success -> {
                    val stateData = (state as SpreadDetailUiState.Success)
                    val spreadResults = if(showToday) stateData.todayResults else stateData.spreadResults

                    Column(Modifier
                        .fillMaxSize()
                        .padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(end = 8.dp).fillMaxWidth()
                        ) {
                            Checkbox(
                                checked = showToday,
                                onCheckedChange = { showToday = it }
                            )
                            Text(
                                text = "Show Today's Results",
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }

                        if (spreadResults.isEmpty()) {
                            Text("No spread drawn today.")
                        } else {
                            spreadResults.forEach {
                                SpreadResultItem(spread = it)
                            }
                        }
                    }
                }
            }
        }
    }


}

@Composable
fun SpreadResultItem(spread: SpreadResult) {
    val formattedDate = remember(spread.createdAt) {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        sdf.format(Date(spread.createdAt))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text("Spread ID: ${spread.spreadDetailId}")
            Text("Selected Cards: ${spread.selectedCardIds.joinToString()}")
            Text("Created At: $formattedDate")
        }
    }
}