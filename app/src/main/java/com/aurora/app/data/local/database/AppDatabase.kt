package com.aurora.app.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.aurora.app.data.local.database.dao.AppDao
import com.aurora.app.data.local.database.entity.PostEntity

@Database(entities = [PostEntity::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appDao(): AppDao
}
