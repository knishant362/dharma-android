package com.aurora.app.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SpreadResult(
    val spreadDetailId: String,
    val selectedCardIds: List<String>,
    val createdAt: Long = System.currentTimeMillis(),
    val adWatched: Boolean = false
) : Parcelable