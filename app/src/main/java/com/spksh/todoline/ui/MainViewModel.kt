package com.spksh.todoline.ui

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spksh.todoline.data.Tag.Tag
import com.spksh.todoline.ui.features.EventFeatures
import com.spksh.todoline.ui.features.SettingsFeatures
import com.spksh.todoline.ui.features.StatisticsFeatures
import com.spksh.todoline.ui.features.TagFeatures
import com.spksh.todoline.ui.features.TaskFeatures
import com.spksh.todoline.ui.features.TimeSlotFeatures
import com.spksh.todoline.ui.features.TimelineFeatures
import com.spksh.todoline.ui.features.TimelinedActivityFeatures
import com.spksh.todoline.ui.model.EventUiModel
import com.spksh.todoline.ui.model.TaskUiModel
import com.spksh.todoline.ui.model.UiStateFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val taskFeaturesFactory: TaskFeatures.Factory,
    private val tagFeaturesFactory: TagFeatures.Factory,
    private val eventFeaturesFactory: EventFeatures.Factory,
    private val timelinedActivityFeaturesFactory: TimelinedActivityFeatures.Factory,
    private val timeSlotFeaturesFactory: TimeSlotFeatures.Factory,
    private val timelineFeaturesFactory: TimelineFeatures.Factory,
    private val statisticsFeaturesFactory: StatisticsFeatures.Factory,
    private val settingsFeaturesFactory: SettingsFeatures.Factory,
    private val uiStateFlowFactory: UiStateFlow.Factory
) : ViewModel() {

    private val _navigationEvents = MutableSharedFlow<NavigationEvent>()
    val navigationEvents = _navigationEvents.asSharedFlow()

    private val uiStateFlow by lazy {
        uiStateFlowFactory.create(zoneId, viewModelScope)
    }

    val uiState by lazy {
        uiStateFlow.uiState
    }

    val taskFeatures by lazy {
        taskFeaturesFactory.create(viewModelScope, uiState, _navigationEvents, zoneId)
    }
    val tagFeatures by lazy {
        tagFeaturesFactory.create(viewModelScope)
    }

    val eventFeatures by lazy {
        eventFeaturesFactory.create(viewModelScope, uiState, _navigationEvents, zoneId)
    }

    val timelinedActivityFeatures by lazy {
        timelinedActivityFeaturesFactory.create(viewModelScope, zoneId)
    }

    val timeSlotFeatures by lazy {
        timeSlotFeaturesFactory.create(viewModelScope, uiState, _navigationEvents)
    }

    val timelineFeatures by lazy {
        timelineFeaturesFactory.create(viewModelScope, zoneId, uiState)
    }

    val statisticsFeatures by lazy {
        statisticsFeaturesFactory.create(viewModelScope, uiState)
    }

    val settingsFeatures by lazy {
        settingsFeaturesFactory.create(viewModelScope)
    }

    fun openTaskScreen(id: Long) {
        viewModelScope.launch {
            _navigationEvents.emit(NavigationEvent.NavigateToTaskScreen(id.toString()))
        }
    }

    fun openEventScreen(id: Long) {
        viewModelScope.launch {
            _navigationEvents.emit(NavigationEvent.NavigateToEventScreen(id.toString()))
        }
    }

    fun popBackStack() {
        viewModelScope.launch {
            _navigationEvents.emit(NavigationEvent.NavigateBack)
        }
    }

    fun openMatrixScreen() {
        viewModelScope.launch {
            _navigationEvents.emit(NavigationEvent.NavigateToMatrixScreen)
        }
    }

    fun openCalendarScreen() {
        viewModelScope.launch {
            _navigationEvents.emit(NavigationEvent.NavigateToCalendarScreen)
        }
    }

    fun openSettingsScreen() {
        viewModelScope.launch {
            _navigationEvents.emit(NavigationEvent.NavigateToSettings.Root)
        }
    }

    fun openScheduleSettingsScreen() {
        viewModelScope.launch {
            _navigationEvents.emit(NavigationEvent.NavigateToSettings.ScheduleScreen)
        }
    }

    fun openStatisticsScreen() {
        viewModelScope.launch {
            _navigationEvents.emit(NavigationEvent.NavigateToSettings.StatisticsScreen)
        }
    }

    fun openTimeSlotScreen(id: Long) {
        viewModelScope.launch {
            _navigationEvents.emit(NavigationEvent.NavigateToSettings.TimeSlotScreen(id.toString()))
        }
    }

    sealed class NavigationEvent {
        data class NavigateToTaskScreen(val id: String) : NavigationEvent()
        data class NavigateToEventScreen(val id: String) : NavigationEvent()
        object NavigateToMatrixScreen : NavigationEvent()
        object NavigateToCalendarScreen : NavigationEvent()
        sealed class NavigateToSettings : NavigationEvent() {
            object Root : NavigateToSettings()
            //object MainScreen : NavigateToSettings()
            object ScheduleScreen : NavigateToSettings()
            object StatisticsScreen : NavigateToSettings()
            data class TimeSlotScreen(val id: String) : NavigateToSettings()
        }
        object NavigateBack : NavigationEvent()
    }

    private val _showTasksWithoutTags = mutableStateOf(true)
    val showTasksWithoutTags
        get() = _showTasksWithoutTags.value

    private val _showCompletedTasks = mutableStateOf(false)
    val showCompletedTasks
        get() = _showCompletedTasks.value

    private var zoneId = ZoneId.systemDefault()

    fun findTaskById(id: Long): TaskUiModel? {
        Log.i("mytag", "find")
        return uiState.value.tasks.find {it.id == id}
    }

    fun toRightZone(deadline: Long?) : Long? {
        return deadline?.let {
            Instant.ofEpochMilli(it)
                .atOffset(ZoneOffset.UTC)
                .toLocalDateTime()
                .atZone(zoneId)
                .toInstant()
                .toEpochMilli()
        }
    }

    fun findTagById(id: Long): Tag? {
        return uiState.value.tags.find {it.id == id}
    }


    fun findEventById(id: Long): EventUiModel? {
        return uiState.value.events.find {it.id == id}
    }

    fun ChangeTasksWithoutTagsVisibility(show: Boolean) {
        _showTasksWithoutTags.value = show
    }
}
