package com.aurora.app.domain.model.dashboard
import com.google.gson.annotations.SerializedName


data class Ringtone(
    val name: String,
    val dname: String,
    val url: String,
    val extension: String,
    val meta: MetaData,
    val ename: String
)

data class MetaData(
    @SerializedName("a")
    val a: Any,
    @SerializedName("d")
    val d: Int,
    @SerializedName("h")
    val h: Any,
    @SerializedName("s")
    val s: Int,
    @SerializedName("w")
    val w: Any
)