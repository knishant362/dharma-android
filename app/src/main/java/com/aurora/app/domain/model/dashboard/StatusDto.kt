package com.aurora.app.domain.model.dashboard

import com.google.gson.Gson
import org.json.JSONObject
import timber.log.Timber

data class StatusDto(
    val id: String,
    val name: String,
    val dname: String,
    val url: String,
    val extension: String,
    val meta: MetaData?,
    val ename: String
)


fun String.toStatusDto(id: String): StatusDto? {
    try {
        val obj = JSONObject(this)
        val name = obj.optString("name", "")
        val dname = obj.optString("dname", "")
        val url = obj.optString("url", "")
        val extension = obj.optString("extension")
        val ename = obj.optString("ename", "")

        val metaObj = obj.optJSONObject("meta")
        val meta = metaObj?.let { Gson().fromJson(it.toString(), MetaData::class.java) }

        return StatusDto(id, name, dname, url, extension, meta, ename)
    } catch (e: Exception) {
        Timber.e(e)
        return null
    }
}