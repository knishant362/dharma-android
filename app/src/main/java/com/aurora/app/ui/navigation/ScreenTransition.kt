package com.aurora.app.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.navigation.NavBackStackEntry
import com.ramcosta.composedestinations.spec.DestinationStyle

object ScreenTransition : DestinationStyle.Animated() {

    override val enterTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?)?
        get() = {
            slideInVertically(
                initialOffsetY = { it }, // Slide in from bottom
                animationSpec = tween(500)
            )
        }

    override val exitTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?)?
        get() = {
            slideOutVertically(
                targetOffsetY = { -it }, // Slide out to top
                animationSpec = tween(500)
            )
        }

    override val popEnterTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?)?
        get() = {
            slideInVertically(
                initialOffsetY = { -it }, // Slide in from top
                animationSpec = tween(500)
            )
        }

    override val popExitTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?)?
        get() = {
            slideOutVertically(
                targetOffsetY = { it }, // Slide out to bottom
                animationSpec = tween(500)
            )
        }
}