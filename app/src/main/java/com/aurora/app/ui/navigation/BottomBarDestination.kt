package com.aurora.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Attractions
import androidx.compose.ui.graphics.vector.ImageVector
import com.ramcosta.composedestinations.generated.destinations.DashboardScreenDestination
import com.ramcosta.composedestinations.generated.destinations.SettingsScreenDestination
import com.ramcosta.composedestinations.generated.destinations.SpreadListScreenDestination
import com.ramcosta.composedestinations.generated.destinations.TarotCardListScreenDestination
import com.ramcosta.composedestinations.spec.DirectionDestinationSpec

enum class BottomBarDestination(
    val direction: DirectionDestinationSpec,
    val icon: ImageVector,
    val label: String
) {
    Dashboard(DashboardScreenDestination, Icons.Default.Attractions, "Dashboard"),
//    SpreadDetail(SpreadListScreenDestination, Icons.Default.Attractions, "Explore"),
    CardsList(TarotCardListScreenDestination, Icons.AutoMirrored.Filled.List, label = "Meanings"),
    Settings(SettingsScreenDestination, Icons.Default.AcUnit, label = "Settings")
}