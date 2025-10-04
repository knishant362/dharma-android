package com.aurora.app.data.remote.response

import com.aurora.app.domain.model.dashboard.Ringtone
import com.google.gson.JsonObject

data class WorkResponse<T>(
    val page: Int,
    val perPage: Int,
    val totalItems: Int,
    val totalPages: Int,
    val items: List<T>,
)

data class Work(
    val collectionId: String,
    val collectionName: String,
    val coverImage: String?,
    val id: String,
    val _id: String,
    val content_type: String,
    val data: Ringtone,
    val mtype: String,
    val extra: JsonObject,
    val extra_en: String,
    val lang: String,
    val topics: String,
    val created_at: String,
    val updated_at: String
)