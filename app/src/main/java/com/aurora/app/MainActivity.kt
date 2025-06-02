package com.aurora.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.aurora.app.designsystem.theme.AuroraTemplateTheme
import com.aurora.app.utils.InAppUpdate
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.firebase.messaging.FirebaseMessaging
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.annotation.NavGraph
import com.ramcosta.composedestinations.generated.NavGraphs
import com.ramcosta.composedestinations.utils.startDestination
import dagger.hilt.android.AndroidEntryPoint
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
//        lifecycleScope.launch(Dispatchers.IO) {
//            OneSignal.Notifications.requestPermission(false)
//        }
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

    DestinationsNavHost(
        navController = navController,
        navGraph = NavGraphs.root,
    )

}