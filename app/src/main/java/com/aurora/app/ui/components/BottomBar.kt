package com.aurora.app.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
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
    NavigationBar(
        modifier = modifier,
    ) {
        BottomBarDestination.entries.forEach { item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector =  item.icon,
                        contentDescription = item.label
                    )
                },
                label = { Text(item.label) },
                selected = item.direction.route == currentDestination,
                onClick = {
                    if (currentDestination != item.direction.route) {
                        navigator.navigate(item.direction){}
                    }
                }
            )
        }

    }
}
