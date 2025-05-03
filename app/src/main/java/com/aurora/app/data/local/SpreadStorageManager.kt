package com.aurora.app.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.aurora.app.data.model.SpreadResult
import com.google.gson.Gson
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import com.google.gson.reflect.TypeToken
import javax.inject.Singleton

@Singleton
class SpreadStorageManager @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {

    private val gson = Gson()
    private val spreadsKey = stringPreferencesKey("saved_spread_results")

    suspend fun saveSpread(spreadDetailId: String, selectedCardIds: List<String>) {
        dataStore.edit { preferences ->
            val existingJson = preferences[spreadsKey]
            val spreadList = if (existingJson != null) {
                gson.fromJson<List<SpreadResult>>(existingJson)
            } else {
                mutableListOf()
            }
            val newResult = SpreadResult(
                spreadDetailId = spreadDetailId,
                selectedCardIds = selectedCardIds
            )
            val updatedList = listOf(newResult) + spreadList
            preferences[spreadsKey] = gson.toJson(updatedList)
        }
    }

    suspend fun getSavedSpreads(): List<SpreadResult> {
        val preferences = dataStore.data.first()
        val json = preferences[spreadsKey] ?: return emptyList()
        return gson.fromJson(json)
    }

    suspend fun getSpreadsBySpreadId(spreadId: String): List<SpreadResult> {
        return getSavedSpreads().filter { it.spreadDetailId == spreadId }
    }

    suspend fun clearSavedSpreads() {
        dataStore.edit { preferences ->
            preferences.remove(spreadsKey)
        }
    }

    suspend fun deleteResult(result: SpreadResult): Boolean {
        var deleted = false
        dataStore.edit { preferences ->
            val existingJson = preferences[spreadsKey]
            val currentList = if (existingJson != null) {
                gson.fromJson<List<SpreadResult>>(existingJson)
            } else {
                emptyList()
            }

            val updatedList = currentList.filterNot {
                val match = it.createdAt == result.createdAt
                if (match) deleted = true
                match
            }

            preferences[spreadsKey] = gson.toJson(updatedList)
        }
        return deleted
    }



    private inline fun <reified T> Gson.fromJson(json: String): T {
        return this.fromJson(json, object : TypeToken<T>() {}.type)
    }

}
