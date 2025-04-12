package com.aurora.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector
import com.aurora.app.ui.screens.destinations.DashboardScreenDestination
import com.aurora.app.ui.screens.destinations.SettingsScreenDestination
import com.aurora.app.ui.screens.destinations.TarotCardListScreenDestination
import com.aurora.app.ui.screens.destinations.TarotSelectScreenDestination
import com.ramcosta.composedestinations.spec.DirectionDestinationSpec

enum class BottomBarDestination(
    val direction: DirectionDestinationSpec,
    val icon: ImageVector,
    val label: String
) {
    CardsList(TarotCardListScreenDestination, Icons.AutoMirrored.Filled.List, label = "Cards"),
    Dashboard(DashboardScreenDestination, Icons.Default.Home, "Home"),
    Tarot(TarotSelectScreenDestination, Icons.Default.Star, "Tarot")
}