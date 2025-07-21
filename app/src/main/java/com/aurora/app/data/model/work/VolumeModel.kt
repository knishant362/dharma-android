package com.aurora.app.data.model.work


import com.google.gson.annotations.SerializedName

data class VolumeModel(
    @SerializedName("id")
    val id: String,
    @SerializedName("title")
    val title: String
)