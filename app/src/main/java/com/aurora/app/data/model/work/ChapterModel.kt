package com.aurora.app.data.model.work


import com.google.gson.annotations.SerializedName

data class ChapterModel(
    @SerializedName("audioFile")
    val audioFile: String,
    @SerializedName("content")
    val content: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("number")
    val number: Int,
    @SerializedName("title")
    val title: String
)