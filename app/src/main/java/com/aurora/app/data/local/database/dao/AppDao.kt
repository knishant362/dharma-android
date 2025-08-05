package com.aurora.app.data.local.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.aurora.app.data.local.database.entity.PostEntity

@Dao
interface AppDao {
    @Query("SELECT * FROM postV2")
    suspend fun getAllPosts(): List<PostEntity>

    @Query("SELECT * FROM postV2")
    suspend fun getPostsByLanguage(): List<PostEntity>

    @Query("SELECT * FROM postV2 WHERE _id = :id")
    fun getPostsById(id: String): List<PostEntity>

    @Query("SELECT * FROM postV2 WHERE mtype = :mType")
    fun getPostsByType(mType: Int): List<PostEntity>
}