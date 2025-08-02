package com.aurora.app.ui.screens.workReading.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.aurora.app.ui.screens.workReading.Chapter
import com.aurora.app.ui.screens.workReading.Volume

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChaptersListView(
    isSheetVisible: Boolean,
    volume: Volume,
    selectedChapter: Chapter,
    chapters: List<Chapter> = emptyList(),
    onChapterClick: (Chapter) -> Unit = {},
    onDismissRequest: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()

    if (!isSheetVisible) return

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState
    ) {
        ChaptersListViewBottomSheet(
            volume = volume,
            selectedChapter = selectedChapter,
            chapters = chapters,
            onClick = onChapterClick
        )
    }
}

@Composable
fun ChaptersListViewBottomSheet(
    modifier: Modifier = Modifier,
    volume: Volume,
    selectedChapter: Chapter,
    chapters: List<Chapter> = emptyList(),
    onClick: (Chapter) -> Unit = {}
) {

    val listState = rememberLazyListState()

    val selectedIndex = remember(selectedChapter, chapters) {
        chapters.indexOfFirst { it.id == selectedChapter.id }.coerceAtLeast(0)
    }

    LaunchedEffect(selectedIndex) {
        if (selectedIndex >= 0) {
            listState.scrollToItem(selectedIndex)
        }
    }

    Column {
        Text(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            text = volume.title,
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )

        LazyColumn(
            modifier = modifier.padding(8.dp),
            state = listState
        ) {
            items(chapters) { chapter ->
                val isSelected = chapter.id == selectedChapter.id
                Text(
                    modifier = Modifier
                        .padding(8.dp)
                        .clickable { onClick(chapter) }
                        .padding(12.dp),
                    text = chapter.title.trim(),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}