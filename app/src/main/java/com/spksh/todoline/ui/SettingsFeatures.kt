package com.spksh.todoline.ui

import com.spksh.todoline.domain.Settings.SetLanguageUseCase
import com.spksh.todoline.domain.Settings.SetShowCompletedTasksUseCase
import com.spksh.todoline.domain.Settings.SetShowMonthTasksUseCase
import com.spksh.todoline.domain.Settings.SetShowTodayTasksUseCase
import com.spksh.todoline.domain.Settings.SetShowWeekTasksUseCase
import com.spksh.todoline.domain.Settings.SetShowWithoutTagsUseCase
import com.spksh.todoline.domain.Settings.SetThemeUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class SettingsFeatures @AssistedInject constructor(
    private val setShowWithoutTagsUseCase: SetShowWithoutTagsUseCase,
    private val setShowCompletedTasksUseCase: SetShowCompletedTasksUseCase,
    private val setShowTodayTasksUseCase: SetShowTodayTasksUseCase,
    private val setShowWeekTasksUseCase: SetShowWeekTasksUseCase,
    private val setShowMonthTasksUseCase: SetShowMonthTasksUseCase,
    private val setThemeUseCase: SetThemeUseCase,
    private val setLanguageUseCase: SetLanguageUseCase,
    @Assisted private val scope: CoroutineScope,
) {
    fun setShowWithoutTags(check: Boolean) {
        scope.launch {
            setShowWithoutTagsUseCase(check)
        }
    }

    fun setShowCompletedTasks(check: Boolean) {
        scope.launch {
            setShowCompletedTasksUseCase(check)
        }
    }

    fun setShowTodayTasks(check: Boolean) {
        scope.launch {
            setShowTodayTasksUseCase(check)
        }
    }

    fun setShowWeekTasks(check: Boolean) {
        scope.launch {
            setShowWeekTasksUseCase(check)
        }
    }

    fun setShowMonthTasks(check: Boolean) {
        scope.launch {
            setShowMonthTasksUseCase(check)
        }
    }

    fun setTheme(check: Boolean) {
        scope.launch {
            setThemeUseCase(check)
        }
    }

    fun setLanguage(check: Boolean) {
        scope.launch {
            setLanguageUseCase(check)
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(
            scope: CoroutineScope,
        ): SettingsFeatures
    }
}