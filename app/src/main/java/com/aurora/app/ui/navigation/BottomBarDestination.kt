package com.aurora.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector
import com.aurora.app.ui.screens.destinations.DashboardScreenDestination
import com.aurora.app.ui.screens.destinations.SettingsScreenDestination
import com.ramcosta.composedestinations.spec.DirectionDestinationSpec

enum class BottomBarDestination(
    val direction: DirectionDestinationSpec,
    val icon: ImageVector,
    val label: String
) {
    Dashboard(DashboardScreenDestination, Icons.Default.Home, "Dashboard"),
    Settings(SettingsScreenDestination, Icons.Default.Face, label = "Settings")
}