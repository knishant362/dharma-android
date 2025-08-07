package com.aurora.app.ui.screens.dasbboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aurora.app.R
import com.aurora.app.data.model.WorkDto
import com.aurora.app.domain.model.dashboard.Featured
import com.aurora.app.ui.components.AuroraImage
import com.aurora.app.ui.components.AuroraTopBar
import com.aurora.app.ui.components.button.AuroraOutlinedButton
import com.aurora.app.utils.toDownloadUrl
import com.aurora.app.utils.toThumb
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.ProfileScreenDestination
import com.ramcosta.composedestinations.generated.destinations.RingtoneScreenDestination
import com.ramcosta.composedestinations.generated.destinations.WallpaperListScreenDestination
import com.ramcosta.composedestinations.generated.destinations.WorkReadingScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class)
@Destination<RootGraph>
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
                    navigator.navigate(ProfileScreenDestination)
                },
            )
        },
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(paddingValues)
            ) {
                Image(
                    modifier = Modifier
                        .fillMaxWidth()
                        .alpha(0.7f),
                    painter = painterResource(R.drawable.bg_main),
                    contentDescription = null,
                    contentScale = ContentScale.Crop
                )
                LazyColumn(
                    modifier = Modifier
                ) {

                    item {
                        TarotFeaturedSection(
                            featuredItems = uiState.featuredItems,
                            onClick = { index, featured ->
                                when(index) {
                                    0 -> navigator.navigate(RingtoneScreenDestination())
                                    1 -> navigator.navigate(WallpaperListScreenDestination())
                                    else -> navigator.navigate(RingtoneScreenDestination())
                                }
                            }
                        )
                    }
                    if (!uiState.isLoading) {
                        item {
                            GreetingSection(uiState.user?.name ?: "")
                        }
                        items(uiState.workSections) { workSection ->
                            WorkListView(
                                categoryName = workSection.categoryName,
                                works = workSection.works,
                                onClick = { work ->
                                    Timber.d("Clicked on work: ${work.title}")
                                    navigator.navigate(WorkReadingScreenDestination(work))
                                }
                            )
                        }
                    } else {
                        item {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                                    .wrapContentWidth(Alignment.CenterHorizontally)
                            )
                        }
                    }

                }
            }
        },
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
            color = Color.Black,
            style = MaterialTheme.typography.titleLarge.copy(textAlign = TextAlign.Center)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Choose a reading:",
            color = Color.Black.copy(alpha = 0.8f),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun TarotFeaturedSection(
    modifier: Modifier = Modifier,
    featuredItems: List<Featured>,
    onClick: (Int, Featured) -> Unit
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
            contentPadding = PaddingValues(horizontal = 16.dp),
            modifier = Modifier
                .fillMaxWidth(),
        ) { page ->
            TarotCarouselCard(
                featured = featuredItems[page],
                onDrawCardsClick = { onClick(page, it) },
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
    onDrawCardsClick: (Featured) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(220.dp)
            .padding(horizontal = 8.dp)
            .clip(RoundedCornerShape(16.dp))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(16.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Image(
            modifier = Modifier.matchParentSize(),
            painter = painterResource(R.drawable.bg_land),
            contentDescription = "",
            contentScale = ContentScale.Crop
        )
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = featured.date,
                color = Color.Black,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(Modifier.height(8.dp))
            Text(
                featured.title,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(Modifier.height(8.dp))
            AuroraOutlinedButton(
                modifier = Modifier,
                text = featured.buttonText,
                textColor = Color.Black,
                onClick = { onDrawCardsClick(featured) }
            )
        }
    }
}


@Composable
fun WorkListView(
    modifier: Modifier = Modifier,
    categoryName: String,
    works: List<WorkDto>,
    onClick: (WorkDto) -> Unit
) {
    Column(modifier = modifier) {
        Text(
            text = categoryName.uppercase(),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(works) { work ->
                WallpaperItemView(
                    imageUrl = work.coverImage?.toDownloadUrl()?.toThumb() ?: "",
                    modifier = Modifier
                        .weight(1f)
                        .width(150.dp)
                        .aspectRatio(1 / 1.5f),
                    onClick = { onClick(work) }
                )
            }
        }
    }
}

@Composable
fun WallpaperItemView(
    imageUrl: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AuroraImage(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(4.dp),
        image = imageUrl,
        onClick = onClick
    )
}