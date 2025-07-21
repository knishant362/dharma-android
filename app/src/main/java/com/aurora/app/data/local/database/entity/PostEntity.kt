package com.aurora.app.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "postV2")
data class PostEntity(
    @PrimaryKey val _id: String,
    val content_type: String,
    val data: String,
    val downloadState: Int,
    val mtype: Int,
    val score: Double,
    val extra: String,
    val extra_en: String?,
    val lang: String,
    val topics: String?,
    val premium_access: Int,
    val elite_access: Int,
    val price: Int,
    val created_at: Double = 0.0,
    val updated_at: Double = 0.0,
    val cls: String?
)
