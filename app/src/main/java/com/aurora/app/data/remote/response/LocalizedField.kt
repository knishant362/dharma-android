package com.aurora.app.data.remote.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LocalizedField(
    val en: String? = null,
    val hi: String? = null,
    val gu: String? = null
): Parcelable
