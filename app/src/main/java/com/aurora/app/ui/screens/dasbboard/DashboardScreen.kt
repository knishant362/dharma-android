package com.aurora.app.ui.screens.dasbboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aurora.app.R
import com.aurora.app.ui.components.AuroraTopBar
import com.aurora.app.ui.components.BottomBar
import com.aurora.app.ui.screens.dasbboard.DashboardViewModel
import com.aurora.app.ui.screens.destinations.DashboardScreenDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@OptIn(ExperimentalMaterial3Api::class)
@Destination
@Composable
fun DashboardScreen(
    navigator: DestinationsNavigator,
    viewModel: DashboardViewModel = hiltViewModel()
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            AuroraTopBar(titleRes = R.string.app_name)
        },
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .padding(paddingValues)
                    .background(color = Color.White)
            ) {
            }
        },
        bottomBar = {
            BottomBar(navigator, DashboardScreenDestination.route)
        }
    )
}

