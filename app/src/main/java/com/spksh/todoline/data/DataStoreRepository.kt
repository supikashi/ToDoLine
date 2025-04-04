package com.spksh.todoline.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DataStoreRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val Context.dataStore by preferencesDataStore(name = "settings")

    private val tasksOrderKey = stringPreferencesKey("tasks_order")

    val tasksOrderFlow: Flow<List<Long>> = context.dataStore.data
        .map { preferences ->
            preferences[tasksOrderKey]?.let { data ->
                data.split(';').map { it.toLong() }
            } ?: emptyList()
        }

    suspend fun saveTasksOrder(list: List<Long>) {
        context.dataStore.edit { preferences ->
            preferences[tasksOrderKey] = list.joinToString(";")
        }
    }
}
