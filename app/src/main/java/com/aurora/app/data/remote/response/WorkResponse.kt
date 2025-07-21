package com.aurora.app.data.remote.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class WorkResponse(
    val items: List<Work>,
    val page: Int,
    val perPage: Int,
    val totalItems: Int,
    val totalPages: Int
): Parcelable

@Parcelize
data class Work(
    val collectionId: String,
    val collectionName: String,
    val coverImage: String,
    val description: LocalizedField?,
    val id: String,
    val postId: String,
    val jsonFile: String,
    val language: String?,
    val title: LocalizedField?,
    val category: String,
    val mType: String? = null
): Parcelable

