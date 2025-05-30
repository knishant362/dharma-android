package com.aurora.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Attractions
import androidx.compose.ui.graphics.vector.ImageVector
import com.aurora.app.ui.screens.destinations.DashboardScreenDestination
import com.aurora.app.ui.screens.destinations.SpreadListScreenDestination
import com.aurora.app.ui.screens.destinations.TarotCardListScreenDestination
import com.ramcosta.composedestinations.spec.DirectionDestinationSpec

enum class BottomBarDestination(
    val direction: DirectionDestinationSpec,
    val icon: ImageVector,
    val label: String
) {
    Dashboard(DashboardScreenDestination, Icons.Default.Attractions, "Dashboard"),
    SpreadDetail(SpreadListScreenDestination, Icons.Default.Attractions, "Explore"),
    CardsList(TarotCardListScreenDestination, Icons.AutoMirrored.Filled.List, label = "Meanings"),
}