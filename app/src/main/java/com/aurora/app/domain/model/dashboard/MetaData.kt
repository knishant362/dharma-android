package com.aurora.app.domain.model.dashboard

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class MetaData(
    @SerializedName("a")
    val a: Double,
    @SerializedName("d")
    val d: Int,
    @SerializedName("h")
    val h: Int,
    @SerializedName("s")
    val s: Int,
    @SerializedName("w")
    val w: Int
): Parcelable