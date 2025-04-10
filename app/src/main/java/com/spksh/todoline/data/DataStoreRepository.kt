package com.spksh.todoline.data

import android.content.Context
import android.content.res.Resources.Theme
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataStoreRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val Context.dataStore by preferencesDataStore(name = "settings")

    private val tasksOrderKey = stringPreferencesKey("tasks_order")
    private val showTaskWithoutTags = stringPreferencesKey("show_task_with_out_tags")
    private val showCompletedTasks = stringPreferencesKey("show_completed_tasks")
    private val language = stringPreferencesKey("language")
    private val theme = stringPreferencesKey("theme")
    private val showTodayTasks = stringPreferencesKey("today")
    private val showWeekTasks = stringPreferencesKey("week")
    private val showMonthTasks = stringPreferencesKey("month")

    val settingsDataFlow: Flow<SettingsData> = context.dataStore.data
        .map { preferences ->
            SettingsData(
                tasksOrder = preferences[tasksOrderKey]?.let { data ->
                    if (data == "") {
                        emptyList()
                    } else {
                        data.split(';').map { it.toLong() }
                    }
                } ?: emptyList(),
                showTasksWithoutTags = preferences[showTaskWithoutTags]?.let { it == "1" } ?: true,
                showCompletedTasks = preferences[showCompletedTasks]?.let { it == "1" } ?: false,
                showTodayTasks = preferences[showTodayTasks]?.let { it == "1" } ?: false,
                showWeekTasks = preferences[showWeekTasks]?.let { it == "1" } ?: false,
                showMonthTasks = preferences[showMonthTasks]?.let { it == "1" } ?: false,
                isEnglish = preferences[language]?.let { it == "1" } ?: true,
                isDarkTheme = preferences[theme]?.let { it == "1" } ?: true,
            )
        }

    suspend fun saveTasksOrder(list: List<Long>) {
        context.dataStore.edit { preferences ->
            if (list.isEmpty()) {
                preferences[tasksOrderKey] = ""
            } else {
                preferences[tasksOrderKey] = list.joinToString(";")
            }
        }
    }

    suspend fun saveShowTasksWithoutTags(check: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[showTaskWithoutTags] = if (!check) "0" else "1"
        }
    }

    suspend fun saveShowCompletedTasks(check: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[showCompletedTasks] = if (!check) "0" else "1"
        }
    }

    suspend fun saveShowTodayTasks(check: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[showTodayTasks] = if (!check) "0" else "1"
        }
    }

    suspend fun saveShowWeekTasks(check: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[showWeekTasks] = if (!check) "0" else "1"
        }
    }

    suspend fun saveShowMonthTasks(check: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[showMonthTasks] = if (!check) "0" else "1"
        }
    }

    suspend fun saveLanguage(check: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[language] = if (!check) "0" else "1"
        }
    }

    suspend fun saveTheme(check: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[theme] = if (!check) "0" else "1"
        }
    }
}

data class SettingsData(
    val tasksOrder: List<Long> = emptyList(),
    val showTasksWithoutTags: Boolean = true,
    val showCompletedTasks: Boolean = true,
    val showTodayTasks: Boolean = true,
    val showWeekTasks: Boolean = true,
    val showMonthTasks: Boolean = true,
    val isEnglish: Boolean = true,
    val isDarkTheme: Boolean = true
)
