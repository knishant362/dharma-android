package com.aurora.app.ui.screens.workReading

import com.aurora.app.data.local.database.entity.PostEntity
import com.aurora.app.data.model.WorkDto

data class WorkReadingUIState(
    val isLoading: Boolean = true,
    val isReadingMode: Boolean = false,
    val errorMessage: String? = null,
    val workDto: WorkDto? = null,
    val postModel: PostEntity? = null,
    val selectedVolume: Pair<String, String>? = null,
    val selectedChapter: Pair<String, String>? = null,
    val volumes: Map<String, String> = emptyMap(),
    val chapters: Map<String, String> = emptyMap(),
    val chapter: String = "",
)

