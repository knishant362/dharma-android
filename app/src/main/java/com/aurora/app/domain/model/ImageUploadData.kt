package com.aurora.app.domain.model

import java.io.File

data class ImageUploadData(
    val title: String,
    val album: String,
    val imageFile: File,
    val resolution: String,
)