package com.aurora.app.ui.screens.workReading

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aurora.app.data.model.WorkDto
import com.aurora.app.ui.components.AuroraImage
import com.aurora.app.ui.components.AuroraTopBar
import com.aurora.app.ui.components.button.AuroraButton
import com.aurora.app.ui.navigation.ScreenTransition
import com.aurora.app.utils.toDownloadUrl
import com.aurora.app.utils.toThumb
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class)
@Destination<RootGraph>(style = ScreenTransition::class)
@Composable
fun WorkReadingScreen(
    workDto: WorkDto,
    navigator: DestinationsNavigator,
    viewModel: WorkReadingViewModel = hiltViewModel()
) {

    LaunchedEffect(Unit) {
        viewModel.initialSetup(workDto)
    }

    val state = viewModel.uiState.value
    Scaffold(
        topBar = {
            AuroraTopBar(
                text = state.workDto?.title?.hi ?: "Work Details Here",
                navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
                onNavigationClick = { navigator.navigateUp() })
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (!state.isReadingMode) {
                ContentsView(
                    uiState = state,
                    onClick = { id -> viewModel.onVolumeSelected(id) }
                )
            } else {

                if (state.postModel == null) {
                    Text(
                        text = "Loading work details...",
                        style = MaterialTheme.typography.bodyLarge,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )
                    return@Column
                }
                WorkContentView(
                    volume = state.selectedVolume ?: Pair("", ""),
                    selectedChapter = state.selectedChapter ?: Pair("", ""),
                    chapterContent = state.chapter,
                    onNextClick = {
                        viewModel.onNextChapterClick()
                    },
                )
            }

        }
    }
}


@Composable
fun ContentsView(
    modifier: Modifier = Modifier,
    uiState: WorkReadingUIState,
    onClick: (selectedVolume: Pair<String, String>) -> Unit
) {
    LazyColumn(modifier = modifier) {
        item {
            val imageUrl = uiState.workDto?.coverImage?.toDownloadUrl()?.toThumb()
            if (imageUrl != null) {
                CardDetailImage(imageUrl = imageUrl, modifier = Modifier.height(260.dp))
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        itemsIndexed(uiState.volumes.entries.toList()) { index, volumeModel ->
            Timber.e("ContentView: Volume- ${volumeModel.value}")
            ContentItemView(
                index = index,
                title = volumeModel.value,
                subtitle = "10 अध्याय",
                modifier = Modifier.padding(vertical = 8.dp),
                onClick = { onClick(volumeModel.toPair()) }
            )
        }
    }
}


@Composable
fun WorkContentView(
    modifier: Modifier = Modifier,
    volume: Pair<String, String>,
    selectedChapter: Pair<String, String>,
    chapterContent: String,
    onNextClick: () -> Unit
) {
    Column {

        LazyColumn(
            modifier = modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Text(
                    modifier = modifier.padding(12.dp),
                    text = volume.second,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = selectedChapter.second,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
            }
            item {
                Text(
                    text = chapterContent ?: "No content available for this chapter",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 18.sp
                )
            }
        }

        ReaderNavBarSection(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            currentPage = 1,
            totalPages = 10,
            onPrevious = { Timber.e("Previous chapter clicked") },
            onNext = { onNextClick() },
            onPageClick = { Timber.e("Page clicked") }
        )
    }
}

@Composable
fun ContentItemView(
    modifier: Modifier = Modifier,
    index: Int,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onClick() }
            .background(Color.White)
            .padding(16.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .aspectRatio(1f)
                .clip(RoundedCornerShape(10.dp))
                .background(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(10.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                modifier = Modifier
                    .padding(3.dp),
                text = "${index + 1}",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Normal,
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = title.trim(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
            contentDescription = "Arrow Icon",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .padding(start = 8.dp)
                .size(24.dp)
        )

    }

}

@Preview
@Composable
fun ContentItemViewPreview(modifier: Modifier = Modifier) {
    ContentItemView(
        modifier = modifier,
        index = 0,
        title = "Volume 1",
        subtitle = "5 Chapters",
        onClick = {}
    )
}

@Preview
@Composable
fun WorkContentViewPreview(modifier: Modifier = Modifier) {
    WorkContentView(
        modifier = modifier.background(color = MaterialTheme.colorScheme.background),
        volume = Pair("1", "Volume 1"),
        selectedChapter = Pair("1", "Chapter 1"),
        chapterContent = "This is the content of Chapter 1",
        onNextClick = {}
    )
}

@Composable
fun CardDetailImage(modifier: Modifier, imageUrl: String) {
    AuroraImage(
        image = imageUrl,
        modifier = modifier
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(12.dp)
            )
            .clip(RoundedCornerShape(14.dp))
            .border(
                width = 2.dp,
                color = Color.Black,
                shape = RoundedCornerShape(16.dp)
            ),
        onClick = {}
    )
}
