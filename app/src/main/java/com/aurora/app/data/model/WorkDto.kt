package com.aurora.app.data.model

import android.os.Parcelable
import com.aurora.app.data.remote.response.LocalizedField
import com.aurora.app.data.remote.response.Work
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
        id = _id,
        title = LocalizedField(en = data.ename, hi = data.dname),
        description = LocalizedField(en = data.ename, hi = data.dname),
        language = lang,
        category = getCategory(mtype),
        jsonFile = "",
        coverImage = if(!coverImage.isNullOrEmpty()) "/${collectionId}/${id}/${coverImage}" else "",
        mType = mtype,
    )
}

fun getCategory(mtype: String): String {
    return when (mtype) {
        "1003" -> "chalisa"
        "1001" -> "books"
        else -> "work"
    }

}