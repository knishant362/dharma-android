package com.aurora.app.data.local

import com.aurora.app.data.model.SpreadResult

interface StorageManager {
    suspend fun saveSpread(spreadDetailId: String, selectedCardIds: List<String>)
    suspend fun getSavedSpreads(): List<SpreadResult>
    suspend fun getSpreadsBySpreadId(spreadId: String): List<SpreadResult>
    suspend fun clearSavedSpreads()
    suspend fun deleteResult(result: SpreadResult): Boolean

    suspend fun setName(name: String)
    suspend fun getName(): String

    suspend fun setDateOfBirth(dob: String)
    suspend fun getDateOfBirth(): String

    suspend fun setGender(gender: String)
    suspend fun getGender(): String

}