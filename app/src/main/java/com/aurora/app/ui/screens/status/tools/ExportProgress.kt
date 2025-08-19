package com.aurora.app.ui.screens.status.tools

data class ExportProgress(
    val progress: Float,
    val status: String,
    val isCompleted: Boolean = false
)