package com.aurora.app.domain.model.spread

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CardInfo(
    val name: String,
    val description: String
): Parcelable