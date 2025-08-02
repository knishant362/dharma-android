package com.aurora.app.ui.screens.workReading

import com.aurora.app.data.local.database.entity.PostEntity
import com.aurora.app.data.model.WorkDto
import com.aurora.app.domain.model.ReaderStyle

data class WorkReadingUIState(
    val isLoading: Boolean = true,
    val isReadingMode: Boolean = false,
    val errorMessage: String? = null,
    val readerStyle: ReaderStyle = ReaderStyle.Default,
    val workDto: WorkDto? = null,
    val postModel: PostEntity? = null,
    val selectedVolume: Volume? = null,
    val selectedChapter: Chapter? = null,
    val volumes: List<Volume> = emptyList(),
    val chapters: List<Chapter> = emptyList(),
    val chapterContent: String = "",
    val chapterIndex: Int = -1,
    val volumeIndex: Int = 0
)

data class Volume(
    val id: String,
    val title: String,
)

data class Chapter(
    val id: String,
    val title: String,
)

