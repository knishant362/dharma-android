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
import androidx.compose.foundation.layout.width
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aurora.app.R
import com.aurora.app.data.model.WorkDto
import com.aurora.app.domain.model.ReaderStyle
import com.aurora.app.domain.model.dashboard.WorkType
import com.aurora.app.ui.components.AuroraImage
import com.aurora.app.ui.components.AuroraTopBar
import com.aurora.app.ui.components.utils.FloatingEffect
import com.aurora.app.ui.navigation.ScreenTransition
import com.aurora.app.ui.screens.workReading.components.ChaptersListView
import com.aurora.app.ui.screens.workReading.components.ReaderNavBarSection
import com.aurora.app.ui.screens.workReading.components.ReaderSettingsView
import com.aurora.app.ui.screens.workReading.components.getFontFamilyFromAssets
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

    val isShowSettings = remember { mutableStateOf(false) }
    val isChaptersListVisible = remember { mutableStateOf(false) }

    val state = viewModel.uiState.value
    Scaffold(
        topBar = {
            AuroraTopBar(
                text = state.workDto?.title?.hi ?: "Work Details Here",
                navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
                onNavigationClick = { navigator.navigateUp() },
                actionIcon = if (state.isReadingMode) ImageVector.vectorResource(R.drawable.ic_text_format) else null,
                onActionClick = { isShowSettings.value = true }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (!state.isReadingMode) {
                ContentsView(
                    uiState = state,
                    onClick = { id -> viewModel.onVolumeSelected(id) }
                )
            } else {

                ReaderSettingsView(
                    isSheetVisible = isShowSettings.value,
                    currentStyle = state.readerStyle,
                    onDismissRequest = {
                        isShowSettings.value = false
                        viewModel.onReaderStyleChange(it)
                    }
                )

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

                if (state.workDto != null && state.workDto.mType == WorkType.CHAPTER.type) {
                    WorkContentView(
                        readerStyle = state.readerStyle,
                        volume = null,
                        selectedChapterTitle = "",
                        chapterContent = state.chapterContent,
                        readerNavBarContent = {}
                    )
                    return@Column
                }

                if (state.selectedVolume == null || state.selectedChapter == null) {
                    Text(
                        text = "Please select a volume and chapter to read.",
                        style = MaterialTheme.typography.bodyLarge,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )
                    return@Column
                } else {


                    ChaptersListView(
                        isSheetVisible = isChaptersListVisible.value,
                        volume = state.selectedVolume,
                        selectedChapter = state.selectedChapter,
                        chapters = state.chapters,
                        onDismissRequest = { isChaptersListVisible.value = false },
                        onChapterClick = { chapter ->
                            Timber.e("ChaptersListView: Chapter selected: ${chapter.title}")
                            isChaptersListVisible.value = false
                            viewModel.onChapterSelected(chapter)
                        }
                    )

                    WorkContentView(
                        readerStyle = state.readerStyle,
                        volume = state.selectedVolume,
                        selectedChapterTitle = state.selectedChapter.title,
                        chapterContent = state.chapterContent,
                        readerNavBarContent = {
                            ReaderNavBarSection(
                                currentPage = state.chapters.indexOf(state.selectedChapter) + 1,
                                totalPages = state.chapters.size,
                                onPrevious = { viewModel.onPreviousChapterClick() },
                                onNext = { viewModel.onNextChapterClick() },
                                onPageClick = { isChaptersListVisible.value = true }
                            )
                        }
                    )
                }
            }

        }
    }
}


@Composable
fun ContentsView(
    modifier: Modifier = Modifier,
    uiState: WorkReadingUIState,
    onClick: (selectedVolume: Volume) -> Unit
) {
    LazyColumn(modifier = modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        item {
            val imageUrl = uiState.workDto?.coverImage?.toDownloadUrl()?.toThumb()
            if (imageUrl != null) {
                CardDetailImage(imageUrl = imageUrl, modifier = Modifier.height(260.dp))
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        itemsIndexed(uiState.volumes) { index, volumeModel ->
            Timber.e("ContentView: Volume- ${volumeModel}")
            ContentItemView(
                index = index,
                title = volumeModel.title,
                subtitle = "10 अध्याय",
                modifier = Modifier.padding(vertical = 8.dp),
                onClick = { onClick(volumeModel) }
            )
        }
    }
}


@Composable
fun WorkContentView(
    modifier: Modifier = Modifier,
    readerStyle: ReaderStyle,
    volume: Volume?,
    selectedChapterTitle: String,
    chapterContent: String,
    readerNavBarContent: @Composable () -> Unit = { }
) {

    val fontSize = readerStyle.fontSize.sp
    val lineHeight = (readerStyle.fontSize * readerStyle.lineHeight).sp
    val fontFamily = getFontFamilyFromAssets(fontName = readerStyle.font)

    Column {

        LazyColumn(
            modifier = modifier
                .weight(1f)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Text(
                    text = volume?.title ?: "",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontFamily = fontFamily
                )
                Text(
                    text = selectedChapterTitle,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    fontFamily = fontFamily
                )
            }
            item {
                Text(
                    text = chapterContent,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = fontSize,
                    lineHeight = lineHeight,
                    fontFamily = fontFamily
                )
            }
        }

        readerNavBarContent()

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
        volume = Volume("1", "Volume 1"),
        selectedChapterTitle = "Chapter 1",
        chapterContent = "This is the content of Chapter 1",
        readerStyle = ReaderStyle.Default,
    )
}
@Composable
fun CardDetailImage(modifier: Modifier, imageUrl: String) {
    FloatingEffect(modifier = modifier) {
        AuroraImage(
            image = imageUrl,
            modifier = Modifier
                .width(164.dp)
                .aspectRatio(1 / 1.5f)
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(16.dp)
                )
                .clip(RoundedCornerShape(16.dp))
                .border(
                    width = 2.dp,
                    color = Color.Black,
                    shape = RoundedCornerShape(16.dp)
                ),
            onClick = {}
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun TopBarPreview(modifier: Modifier = Modifier) {
    AuroraTopBar(
        text = "Work Details Here",
        navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
        onNavigationClick = { },
        actionIcon = ImageVector.vectorResource(R.drawable.ic_text_format),
        onActionClick = {}
    )
}
