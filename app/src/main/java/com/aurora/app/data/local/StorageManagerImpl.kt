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
class StorageManagerImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : StorageManager {

    private val gson = Gson()

    override suspend fun saveSpread(spreadDetailId: String, selectedCardIds: List<String>) {
        dataStore.edit { preferences ->
            val existingJson = preferences[PreferenceKeys.Spreads]
            val spreadList = if (existingJson != null) {
                gson.fromJson<List<SpreadResult>>(existingJson)
            } else {
                emptyList()
            }
            val newResult = SpreadResult(
                spreadDetailId = spreadDetailId,
                selectedCardIds = selectedCardIds
            )
            val updatedList = listOf(newResult) + spreadList
            preferences[PreferenceKeys.Spreads] = gson.toJson(updatedList)
        }
    }

    override suspend fun getSavedSpreads(): List<SpreadResult> {
        val json = dataStore.data.first()[PreferenceKeys.Spreads] ?: return emptyList()
        return gson.fromJson(json)
    }

    override suspend fun getSpreadsBySpreadId(spreadId: String): List<SpreadResult> {
        return getSavedSpreads().filter { it.spreadDetailId == spreadId }
    }

    override suspend fun clearSavedSpreads() {
        dataStore.edit { it.remove(PreferenceKeys.Spreads) }
    }

    override suspend fun deleteResult(result: SpreadResult): Boolean {
        var deleted = false
        dataStore.edit { preferences ->
            val currentList = preferences[PreferenceKeys.Spreads]
                ?.let { gson.fromJson<List<SpreadResult>>(it) }
                ?: emptyList()

            val updatedList = currentList.filterNot {
                val match = it.createdAt == result.createdAt
                if (match) deleted = true
                match
            }

            preferences[PreferenceKeys.Spreads] = gson.toJson(updatedList)
        }
        return deleted
    }

    override suspend fun setName(name: String) {
        dataStore.edit { it[PreferenceKeys.Name] = name }
    }

    override suspend fun getName(): String {
        return dataStore.data.first()[PreferenceKeys.Name] ?: ""
    }

    override suspend fun setDateOfBirth(dob: String) {
        dataStore.edit { it[PreferenceKeys.DOB] = dob }
    }

    override suspend fun getDateOfBirth(): String {
        return dataStore.data.first()[PreferenceKeys.DOB] ?: ""
    }

    override suspend fun setGender(gender: String) {
        dataStore.edit { it[PreferenceKeys.Gender] = gender }
    }

    override suspend fun getGender(): String {
        return dataStore.data.first()[PreferenceKeys.Gender] ?: ""
    }

    // Gson helper extension
    private inline fun <reified T> Gson.fromJson(json: String): T {
        return this.fromJson(json, object : TypeToken<T>() {}.type)
    }
}

private object PreferenceKeys {
    val Spreads = stringPreferencesKey("saved_spread_results")
    val Name = stringPreferencesKey("user_name")
    val DOB = stringPreferencesKey("user_dob")
    val Gender = stringPreferencesKey("user_gender")
}