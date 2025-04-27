package com.spksh.todoline.ui.model

import com.spksh.todoline.data.SettingsData
import com.spksh.todoline.domain.Event.GetEventsFlowUseCase
import com.spksh.todoline.domain.Settings.GetSettingsFlowUseCase
import com.spksh.todoline.domain.Tag.GetTagsFlowUseCase
import com.spksh.todoline.domain.Task.GetTasksFlowUseCase
import com.spksh.todoline.domain.TimeSlot.GetTimeSlotsFlowUseCase
import com.spksh.todoline.domain.TimelinedActivity.GetActivitiesFlowUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDateTime
import java.time.ZoneId

class UiStateFlow @AssistedInject constructor(
    getTasksFlowUseCase: GetTasksFlowUseCase,
    getTagsFlowUseCase: GetTagsFlowUseCase,
    getEventsFlowUseCase: GetEventsFlowUseCase,
    getActivitiesFlowUseCase: GetActivitiesFlowUseCase,
    getTimeSlotsFlowUseCase: GetTimeSlotsFlowUseCase,
    getSettingsFlowUseCase: GetSettingsFlowUseCase,
    @Assisted val zoneId: ZoneId,
    @Assisted val scope: CoroutineScope
) {
    val uiState = combine(
        getTasksFlowUseCase(),
        getTagsFlowUseCase(),
        getEventsFlowUseCase(),
        getActivitiesFlowUseCase(),
        getTimeSlotsFlowUseCase()
    ) { tasks, tags, events, activities, timeSlots ->
        UiState.State(
            tasks = tasks.map { TaskUiModel(it, zoneId) }.sortedBy {  it.deadlineLocal ?: if (it.urgency < 6) LocalDateTime.MAX else LocalDateTime.MIN },
            tags = tags,
            events = events.map { EventUiModel(it, zoneId) }.sortedBy { it.endTime }.sortedBy { it.startTime },
            activities = activities.map { ActivityUiModel(it, zoneId) }.sortedBy { it.endTime }.sortedBy { it.startTime },
            timeSlots = timeSlots.map { TimeSlotUiModel(it) }.sortedBy { it.startTime },
            settings = SettingsUi()
        )
    }.combine(getSettingsFlowUseCase()) { state, settings ->
        state.copy(settings = SettingsUi(settings, state.tasks))
    }.stateIn(
        scope,
        SharingStarted.WhileSubscribed(5000),
        UiState.State(
            emptyList(),
            emptyList(),
            emptyList(),
            emptyList(),
            emptyList(),
            SettingsUi()
        )
    )

    @AssistedFactory
    interface Factory {
        fun create(
            zoneId: ZoneId,
            scope: CoroutineScope
        ): UiStateFlow
    }
}

data class SettingsUi(
    val tasksOrder: List<TaskUiModel> = emptyList(),
    val showTasksWithoutTags: Boolean = true,
    val showCompletedTasks: Boolean = true,
    val showTodayTasks: Boolean = true,
    val showWeekTasks: Boolean = true,
    val showMonthTasks: Boolean = true,
    val isEnglish: Boolean = true,
    val isDarkTheme: Boolean = true,
) {
    fun toData() : SettingsData {
        return SettingsData(
            tasksOrder = this.tasksOrder.map { it.id },
            showTasksWithoutTags = showTasksWithoutTags,
            showCompletedTasks = showCompletedTasks,
            showTodayTasks = showTodayTasks,
            showWeekTasks = showWeekTasks,
            showMonthTasks = showMonthTasks,
            isEnglish = isEnglish,
            isDarkTheme = isDarkTheme
        )
    }

    constructor(data: SettingsData, tasks: List<TaskUiModel>) : this(
        tasksOrder = data.tasksOrder.mapNotNull { id -> tasks.find { it.id == id} },
        showTasksWithoutTags = data.showTasksWithoutTags,
        showCompletedTasks = data.showCompletedTasks,
        showTodayTasks = data.showTodayTasks,
        showWeekTasks = data.showWeekTasks,
        showMonthTasks = data.showMonthTasks,
        isEnglish = data.isEnglish,
        isDarkTheme = data.isDarkTheme
    )
}