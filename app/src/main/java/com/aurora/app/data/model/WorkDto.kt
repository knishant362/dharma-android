package com.aurora.app.data.model

import android.os.Parcelable
import com.aurora.app.BuildConfig
import com.aurora.app.data.remote.response.LocalizedField
import com.aurora.app.data.remote.response.Work
import com.aurora.app.utils.Constants
import kotlinx.parcelize.Parcelize

@Parcelize
data class WorkDto(
    val id: String,
    val category: String,
    val title: LocalizedField?,
    val description: LocalizedField?,
    val language: String?,
    val jsonFile: String,
    val coverImage: String?,
    val mType: String?,
): Parcelable

fun Work.toWorkDto(): WorkDto {
    return WorkDto(
        id = postId,
        title = title,
        description = description,
        language = language,
        jsonFile = "${collectionId}/${id}/${jsonFile}",
        category = category,
        coverImage = "/${collectionId}/${id}/${coverImage}",
        mType = mType
    )
}
