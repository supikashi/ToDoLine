package com.spksh.todoline.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DataStoreRepository(private val dataStore: DataStore<Preferences>) {

    /*private val TAGS_LIST_KEY = stringPreferencesKey("tags_list")

    val tagsList: Flow<List<String>> = dataStore.data.map { preferences ->
        preferences[TAGS_LIST_KEY]?.split(";") ?: emptyList()
    }

    suspend fun saveList(list: List<String>) {
        dataStore.edit { preferences ->
            preferences[TAGS_LIST_KEY] = list.joinToString(";")
        }
    }*/
}
