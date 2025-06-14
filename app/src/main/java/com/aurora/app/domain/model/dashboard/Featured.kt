package com.aurora.app.domain.model.dashboard

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Featured(
    val id: Long,
    val date: String,
    val title: String,
    val buttonText: String
): Parcelable
