package com.aurora.app.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TarotCard(
    val id: String,
    val name: String,
    val type: String,
//    val suit: String?,
    val affirmation: String,
    val description: String,
//    val imageRes: String,
    val keywords: List<String>?
) : Parcelable
