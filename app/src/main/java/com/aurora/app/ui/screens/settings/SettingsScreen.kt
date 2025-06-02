package com.aurora.app.ui.screens.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.aurora.app.R
import com.aurora.app.ui.components.AuroraTopBar
import com.aurora.app.ui.navigation.ScreenTransition
import com.aurora.app.utils.AppNavigationHelper
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@OptIn(ExperimentalMaterial3Api::class)
@Destination<RootGraph>(style = ScreenTransition::class)
@Composable
fun SettingsScreen(navigator: DestinationsNavigator) {
    Scaffold(
        topBar = {
            AuroraTopBar(
                titleRes = R.string.app_name,
                navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
                onNavigationClick = {
                    navigator.navigateUp()
                }
            )
        },
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .padding(paddingValues)
            ) {
                OptionSection()
            }
        }
    )
}

@Composable
fun OptionSection(modifier: Modifier = Modifier) {

    val context = LocalContext.current
    val appNavigationHelper = remember { AppNavigationHelper(context) }

    Column(modifier = modifier.fillMaxSize()) {
        AppPanel()
        OptionItem(title = stringResource(R.string.rate_us), icon = Icons.Filled.Star, onClick = { appNavigationHelper.openPlayStore() })
        OptionItem(title = stringResource(R.string.share), icon = Icons.Filled.Share, onClick = { appNavigationHelper.shareApp() })
        OptionItem(title = stringResource(R.string.privacy_policy), icon = Icons.Filled.Info, onClick = { appNavigationHelper.openPrivacyPolicy() })
        OptionItem(title = stringResource(R.string.support), icon = Icons.Filled.Email, onClick = { appNavigationHelper.openSupportEmail()})
    }
}

@Composable
fun OptionItem(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .padding(horizontal = 8.dp)
            .padding(8.dp)
            .border(shape = RoundedCornerShape(8.dp), width = 1.dp, color = Color.LightGray)
            .clickable { onClick() }
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            modifier = Modifier
                .padding(16.dp)
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(8.dp),
            contentDescription = null,
            imageVector = icon,
            colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.background)
        )

        Spacer(modifier = Modifier.width(16.dp))
        Text(text = title)
        Spacer(modifier = Modifier.weight(1f))
        Image(
            modifier = Modifier
                .padding(8.dp),
            contentDescription = null,
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.primary)
        )
    }
}

@Composable
fun AppPanel(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .padding(8.dp)
            .border(shape = RoundedCornerShape(8.dp), width = 1.dp, color = Color.LightGray)
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            modifier = Modifier
                .padding(16.dp)
                .padding(vertical = 16.dp)
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(8.dp)
                .size(136.dp)
                .clip(RoundedCornerShape(16.dp)),
            contentDescription = null,
            painter = painterResource(id = R.drawable.app_icon),
        )
    }
}