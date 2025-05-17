package com.aurora.app.ui.screens.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.aurora.app.R
import com.aurora.app.ui.screens.NavGraphs
import com.aurora.app.ui.screens.destinations.DashboardScreenDestination
import com.aurora.app.ui.screens.destinations.SpreadDetailScreenDestination
import com.aurora.app.ui.screens.destinations.TarotCardListScreenDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay

@RootNavGraph(true)
@Destination
@Composable
fun SplashScreen(modifier: Modifier = Modifier, navigator: DestinationsNavigator) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.surface),
        contentAlignment = Alignment.Center
    ) {

        Image(
            modifier = Modifier
                .size(160.dp)
                .clip(shape = CircleShape),
            painter = painterResource(id = R.drawable.app_icon),
            contentDescription = stringResource(id = R.string.app_name)
        )
    }

    LaunchedEffect(Unit) {
        coroutineScope {
            delay(1000)
            navigator.navigate(DashboardScreenDestination) {
                popUpTo(NavGraphs.root) {
                    saveState = true
                }
            }
        }
    }
}