package com.aurora.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.aurora.app.designsystem.theme.AuroraTemplateTheme
import com.aurora.app.ui.screens.NavGraphs
import com.aurora.app.utils.InAppUpdate
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.firebase.messaging.FirebaseMessaging
import com.onesignal.OneSignal
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.utils.startDestination
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            AuroraTemplateTheme {
                AuroraApp()
            }
        }
        lifecycleScope.launch(Dispatchers.IO) {
            OneSignal.Notifications.requestPermission(false)
        }
//        appReviewFlow()
        inAppUpdate()
        subscripbeTopic()
    }

    private fun subscripbeTopic() {
        lifecycleScope.launch {
            FirebaseMessaging.getInstance().subscribeToTopic("All User")
        }
    }

    private fun inAppUpdate() {
        InAppUpdate.checkUpdate(this@MainActivity)
    }

    private fun appReviewFlow() {
        lifecycleScope.launch {
            delay(10_000) // 10 second delay for better UX
            runCatching {
                val manager = ReviewManagerFactory.create(this@MainActivity)
                val reviewInfo = manager.requestReviewFlow().await()
                manager.launchReviewFlow(this@MainActivity, reviewInfo).await()
            }
        }

    }
}

@Composable
fun AuroraApp() {

    val navController: NavHostController = rememberNavController()

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.bg_endless_constellation),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        DestinationsNavHost(
            navController = navController,
            navGraph = NavGraphs.root,
            startRoute = NavGraphs.root.startDestination
        )
    }

}