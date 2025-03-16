package com.aurora.app.data.local.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AppDataStore@Inject constructor(
    private val dataStore: DataStore<Preferences>
)  {
    private val IS_FIRST_USER = booleanPreferencesKey("isFirstUser")
    private val APP_OPEN_COUNT = intPreferencesKey("appOpenCount")

    val isUserFirstTime: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[IS_FIRST_USER] ?: true
        }

    val appOpenCount: Flow<Int> = dataStore.data
        .map { preferences ->
            preferences[APP_OPEN_COUNT] ?: 0
        }

    suspend fun changeFirstTime(isFirstTime: Boolean) {
        dataStore.edit {
            it[IS_FIRST_USER] = isFirstTime
        }
    }

    suspend fun updateAppOpenCount() {
        dataStore.edit {
            it[APP_OPEN_COUNT] = (it[APP_OPEN_COUNT] ?: 0) + 1
        }
    }
}