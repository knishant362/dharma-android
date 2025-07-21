package com.aurora.app.ui.screens.workReading

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aurora.app.data.model.WorkDto
import com.aurora.app.ui.components.AuroraImage
import com.aurora.app.ui.components.AuroraTopBar
import com.aurora.app.ui.components.button.AuroraButton
import com.aurora.app.ui.navigation.ScreenTransition
import com.aurora.app.utils.Decrypt.decryptBookText
import com.aurora.app.utils.toDownloadUrl
import com.aurora.app.utils.toThumb
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

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
                text = state.workDto?.title?.en ?: "Work Details Here",
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
                    chapters = state.chapters,
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

        items(uiState.volumes.entries.toList()) { volumeModel ->
            ContentItemView(
                title = volumeModel.value,
                subtitle = "${volumeModel.value} अध्याय",
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
    chapters: Map<String, String>,
    selectedChapter: Pair<String, String>,
    chapterContent: String,
    onNextClick: () -> Unit
) {
    LazyColumn(modifier = modifier) {
        item {
            Text(
                text = "पुस्तक ${volume.second} : ${chapters.size} अध्याय",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "अध्याय $selectedChapter : ${chapters.getOrDefault(selectedChapter.first, "Unknown")}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        item {
            Text(
                text = decryptBookText(chapterContent) ?: "No content available for this chapter",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        item {
            AuroraButton("Next", modifier = Modifier.padding(16.dp), onClick = onNextClick)
        }
    }
}

@Composable
fun ContentItemView(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
            .clickable { onClick() },
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }

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
