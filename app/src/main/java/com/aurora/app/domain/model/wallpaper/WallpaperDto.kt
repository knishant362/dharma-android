package com.aurora.app.domain.model.wallpaper

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import org.json.JSONObject
import timber.log.Timber


data class WallpaperDto(
    val id: String,
    val name: String,
    val dname: String,
    val url: String,
    val extension: String,
    val meta: MetaData?,
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


fun String.toWallpaperDto(wallpaperId: String): WallpaperDto? {
    try {
        val obj = JSONObject(this)
        val name = obj.optString("name", "")
        val dname = obj.optString("dname", "")
        val url = obj.optString("url", "")
        val extension = obj.optString("extension")
        val ename = obj.optString("ename", "")

        val metaObj = obj.optJSONObject("meta")
        val meta = metaObj?.let { Gson().fromJson(it.toString(), MetaData::class.java) }

        return WallpaperDto(wallpaperId, name, dname, url, extension, meta, ename)
    } catch (e: Exception) {
        Timber.e(e)
        return null
    }
}