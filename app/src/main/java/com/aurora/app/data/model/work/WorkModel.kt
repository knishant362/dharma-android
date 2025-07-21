package com.aurora.app.data.model.work

import com.google.gson.annotations.SerializedName

data class WorkModel(
    @SerializedName("coverImage")
    val coverImage: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("volumes")
    val volumeModels: List<VolumeModel>
)