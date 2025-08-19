package com.aurora.app.ui.screens.status.model

import com.aurora.app.R

// Data class
sealed class SharePlatform(
    val name: String,
    val icon: Int,
) {
    data object WhatsApp : SharePlatform(
        name = "WhatsApp",
        icon = R.drawable.ic_whatsapp,
    )

    data object Instagram : SharePlatform(
        name = "Instagram",
        icon = R.drawable.ic_instagram,
    )

    data object Facebook : SharePlatform(
        name = "Facebook",
        icon = R.drawable.ic_facebook,
    )

    data object More : SharePlatform(
        name = "More",
        icon = R.drawable.ic_more,
    )
}