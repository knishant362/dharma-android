package com.aurora.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AppSettingsAlt
import androidx.compose.material.icons.filled.Attractions
import androidx.compose.ui.graphics.vector.ImageVector
import com.aurora.app.ui.screens.destinations.SettingsScreenDestination
import com.aurora.app.ui.screens.destinations.SpreadDetailScreenDestination
import com.aurora.app.ui.screens.destinations.TarotCardListScreenDestination
import com.ramcosta.composedestinations.spec.DirectionDestinationSpec

enum class BottomBarDestination(
    val direction: DirectionDestinationSpec,
    val icon: ImageVector,
    val label: String
) {
    SpreadDetail(SpreadDetailScreenDestination, Icons.Default.Attractions, "Tarot"),
    CardsList(TarotCardListScreenDestination, Icons.AutoMirrored.Filled.List, label = "Cards"),
    Settings(SettingsScreenDestination, Icons.Default.AppSettingsAlt, label = "Settings")
}