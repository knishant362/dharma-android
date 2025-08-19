package com.aurora.app.ui.screens.status.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserProfile(
    val name: String,
    val businessName: String,
    val address: String,
    val profileImage: String
): Parcelable

data class SocialStats(
    val whatsappShares: Int,
    val likes: Int,
    val views: Int
)