package com.aurora.app.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.aurora.app.ui.navigation.BottomBarDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Composable
fun BottomBar(
    navigator: DestinationsNavigator,
    currentDestination: String,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme

    NavigationBar(
        modifier = modifier,
        containerColor = colorScheme.background,
        contentColor = colorScheme.onBackground
    ) {
        BottomBarDestination.entries.forEach { item ->
            val selected = item.direction.route == currentDestination
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        tint = if (selected) colorScheme.primary else colorScheme.onBackground
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        color = if (selected) colorScheme.primary else colorScheme.onBackground
                    )
                },
                selected = selected,
                onClick = {
                    if (!selected) navigator.navigate(item.direction) {}
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = colorScheme.primary,
                    unselectedIconColor = colorScheme.onBackground,
                    selectedTextColor = colorScheme.primary,
                    unselectedTextColor = colorScheme.onBackground,
                    indicatorColor = colorScheme.surfaceVariant
                )
            )
        }
    }
}
