package com.aurora.app.ui.screens.dasbboard

import androidx.activity.ComponentActivity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aurora.app.R
import com.aurora.app.data.model.WorkDto
import com.aurora.app.domain.model.dashboard.Featured
import com.aurora.app.ui.components.AuroraImage
import com.aurora.app.ui.components.AuroraTopBar
import com.aurora.app.ui.components.utils.ForceUpdateChecker
import com.aurora.app.ui.screens.workReading.components.getFontFamilyFromAssets
import com.aurora.app.utils.toDownloadUrl
import com.aurora.app.utils.toThumb
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.HoroscopeScreenDestination
import com.ramcosta.composedestinations.generated.destinations.ProfileScreenDestination
import com.ramcosta.composedestinations.generated.destinations.RingtoneScreenDestination
import com.ramcosta.composedestinations.generated.destinations.StatusMakerScreenDestination
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

    val context = LocalContext.current
    ForceUpdateChecker {
        (context as? ComponentActivity)?.finishAffinity()
    }

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

                    if (uiState.showUpgradeDialog) {
                        item { UpdatingDialog() }
                    }

                    item {
                        TarotFeaturedSection(
                            featuredItems = uiState.featuredItems,
                            onClick = { index, featured ->
                                when (index) {
                                    0 -> navigator.navigate(RingtoneScreenDestination())
                                    1 -> navigator.navigate(WallpaperListScreenDestination())
                                    2 -> navigator.navigate(StatusMakerScreenDestination())
                                    else -> navigator.navigate(RingtoneScreenDestination())
                                }
                            }
                        )
                    }
                    if (!uiState.isLoading) {
                        item {
                            GreetingSection(uiState.user?.name ?: "")
                        }
                        item {
                            CategoriesGrid(
                                uiState.categories,
                                onCategoryClick = { index, category ->
                                    when (index) {
                                        0 -> navigator.navigate(RingtoneScreenDestination())
                                        1 -> navigator.navigate(WallpaperListScreenDestination())
                                        2 -> navigator.navigate(StatusMakerScreenDestination())
                                        3 -> navigator.navigate(HoroscopeScreenDestination())
                                        else -> navigator.navigate(RingtoneScreenDestination())
                                    }
                                })
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
            .height(240.dp)
            .padding(horizontal = 8.dp)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                shape = RoundedCornerShape(20.dp)
            )
            .clip(RoundedCornerShape(20.dp))
            .padding(4.dp)
            .clip(RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center
    ) {
        Image(
            modifier = Modifier.matchParentSize(),
            painter = painterResource(featured.background),
            contentDescription = "",
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = featured.date,
                color = Color.White,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Medium
                ),
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = featured.title,
                color = Color.White,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    shadow = Shadow(
                        color = Color.Black.copy(alpha = 0.8f),
                        offset = Offset(2f, 2f),
                        blurRadius = 4f
                    ),
                    lineHeight = 28.sp
                ),
                modifier = Modifier
                    .padding(horizontal = 8.dp)
            )

            Spacer(Modifier.height(16.dp))

            Surface(
                modifier = Modifier
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(25.dp)
                    )
                    .clickable { onDrawCardsClick(featured) },
                shape = RoundedCornerShape(25.dp),
                color = Color.White.copy(alpha = 0.9f),
                border = BorderStroke(
                    width = 1.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                            MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f)
                        )
                    )
                )
            ) {
                Text(
                    text = featured.buttonText,
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)
                )
            }
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
            modifier = Modifier.padding(start = 28.dp, end = 28.dp, top = 16.dp, bottom = 4.dp),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        LazyRow(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 24.dp)
        ) {
            items(works) { work ->
                WallpaperItemView(
                    imageUrl = work.coverImage?.toDownloadUrl()?.toThumb() ?: "",
                    modifier = Modifier
                        .weight(1f)
                        .width(164.dp)
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

data class CategoryItem(
    val title: String,
    val subtitle: String,
    val icon: Int,
    val gradient: List<Color>
)

@Composable
fun CategoriesGrid(categories: List<CategoryItem>, onCategoryClick: (Int, CategoryItem) -> Unit) {

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .height(264.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        itemsIndexed(categories) { index, category ->
            CategoryCard(index, category, onClick = onCategoryClick)
        }
    }
}

@Composable
fun CategoryCard(index: Int, category: CategoryItem, onClick: (Int, CategoryItem) -> Unit) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clickable {
                onClick(index, category)
            },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.linearGradient(category.gradient))
                .padding(24.dp)
        ) {
            Column {
                Text(
                    text = category.title,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = category.subtitle,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }

            Image(
                painter = painterResource(id = category.icon),
                contentDescription = null,
                modifier = Modifier
                    .size(48.dp)
                    .align(Alignment.BottomEnd),
                contentScale = ContentScale.Crop
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdatingDialog(
    onDismiss: () -> Unit = {}
) {

    BasicAlertDialog(
        onDismissRequest = { onDismiss() }
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .padding(16.dp)
                .wrapContentWidth()
                .wrapContentWidth(Alignment.CenterHorizontally),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {

            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .widthIn(min = 200.dp, max = 280.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.CloudDownload,
                    contentDescription = "Updating",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(48.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Updating App Content…",
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Please wait while the latest content is downloaded.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(16.dp))

                CircularProgressIndicator(
                    modifier = Modifier.size(32.dp),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
