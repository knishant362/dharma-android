package com.aurora.app.ui.screens.dasbboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aurora.app.R
import com.aurora.app.domain.model.dashboard.Featured
import com.aurora.app.domain.model.dashboard.TarotOption
import com.aurora.app.ui.components.AuroraTopBar
import com.aurora.app.ui.components.BottomBar
import com.aurora.app.ui.components.button.AuroraOutlinedButton
import com.aurora.app.ui.screens.destinations.DashboardScreenDestination
import com.aurora.app.ui.screens.destinations.SettingsScreenDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@OptIn(ExperimentalMaterial3Api::class)
@Destination
@Composable
fun DashboardScreen(
    navigator: DestinationsNavigator,
    viewModel: DashboardViewModel = hiltViewModel()
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            AuroraTopBar(
                titleRes = R.string.app_name,
                actionIcon = Icons.Rounded.AccountCircle,
                onActionClick = {
                    navigator.navigate(SettingsScreenDestination)
                }
            )
        },
        content = { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .padding(paddingValues)
                    .background(Color(0xFF121017))
            ) {

                item {
                    TarotFeaturedSection(
                        featuredItems = uiState.featuredItems,
                        onDrawCardsClick = { }
                    )
                }

                item {
                    GreetingSection("Nishant")
                    TarotOptionsGrid(
                        uiState.tarotOptions,
                        onClick = {}
                    )
                }
            }
        },
        bottomBar = {
            BottomBar(navigator, DashboardScreenDestination.route)
        }
    )
}

@Composable
fun GreetingSection(
    userName: String,
    modifier: Modifier = Modifier,
) {

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            "WHAT AWAITS YOU TODAY, $userName?",
            color = Color.White,
            style = MaterialTheme.typography.titleLarge.copy(textAlign = TextAlign.Center)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Choose a reading:",
            color = Color.White.copy(alpha = 0.8f),
            style = MaterialTheme.typography.bodyLarge
        )

    }

}

@Composable
fun TarotFeaturedSection(
    modifier: Modifier = Modifier,
    featuredItems: List<Featured>,
    onDrawCardsClick: () -> Unit
) {
    if (featuredItems.isEmpty()) return

    val pagerState = rememberPagerState(initialPage = 0, pageCount = { featuredItems.size })

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HorizontalPager(
            state = pagerState,
            pageSize = PageSize.Fill,
            modifier = Modifier
                .fillMaxWidth(),
        ) { page ->
            TarotCarouselCard(
                featured = featuredItems[page],
                onDrawCardsClick = onDrawCardsClick,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.Center) {
            repeat(featuredItems.size) { index ->
                val selected = pagerState.currentPage == index
                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .size(if (selected) 10.dp else 8.dp)
                        .background(
                            color = if (selected) MaterialTheme.colorScheme.primary else Color.LightGray,
                            shape = RoundedCornerShape(50)
                        )
                )
            }
        }
    }
}


@Composable
fun TarotCarouselCard(
    featured: Featured,
    onDrawCardsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(200.dp)
            .background(color = Color(0xFF40315D)),
        contentAlignment = Alignment.Center
    ) {
        Image(
            modifier = Modifier.fillMaxWidth(),
            painter = painterResource(R.drawable.bg_gradient),
            contentDescription = "",
            contentScale = ContentScale.FillWidth
        )
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("May 17", color = Color.White, style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(8.dp))
            Text(
                featured.title,
                color = Color.White,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(Modifier.height(16.dp))
            AuroraOutlinedButton(
                text = featured.buttonText,
                onClick = onDrawCardsClick
            )
        }
    }
}

@Composable
fun TarotOptionCard(
    title: String,
    backgroundColor: Color,
    iconRes: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = painterResource(id = iconRes),
                    contentDescription = title,
                    modifier = Modifier
                        .size(100.dp)
                        .padding(8.dp),
                    contentScale = ContentScale.FillWidth
                )
                Text(
                    modifier = Modifier.padding(4.dp),
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun TarotOptionsGrid(
    tarotOptions: List<TarotOption>,
    onClick: (TarotOption) -> Unit
) {
    val rows = tarotOptions.chunked(2)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        rows.forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                rowItems.forEach { tarotOption ->
                    val backgroundColor =
                        if (tarotOption.id % 2 == 0L) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.background
                    TarotOptionCard(
                        title = tarotOption.title,
                        backgroundColor = backgroundColor,
                        iconRes = tarotOption.iconRes,
                        onClick = { onClick.invoke(tarotOption) },
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                    )
                }

                // If odd item count, fill remaining space
                if (rowItems.size < 2) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}