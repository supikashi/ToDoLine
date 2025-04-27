package com.spksh.todoline.ui.features

import com.spksh.todoline.domain.TimeSlot.AddTimeSlotUseCase
import com.spksh.todoline.domain.TimeSlot.DeleteTimeSlotUseCase
import com.spksh.todoline.domain.TimeSlot.UpdateTimeSlotUseCase
import com.spksh.todoline.ui.MainViewModel.NavigationEvent
import com.spksh.todoline.ui.model.TimeSlotUiModel
import com.spksh.todoline.ui.model.UiState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TimeSlotFeatures @AssistedInject constructor(
    private val addTimeSlotUseCase: AddTimeSlotUseCase,
    private val updateTimeSlotUseCase: UpdateTimeSlotUseCase,
    private val deleteTimeSlotUseCase: DeleteTimeSlotUseCase,
    @Assisted private val scope: CoroutineScope,
    @Assisted private val flow: StateFlow<UiState.State>,
    @Assisted private val navEvents: MutableSharedFlow<NavigationEvent>,
) {
    fun add(timeSlot: TimeSlotUiModel) {
        scope.launch {
            val id = addTimeSlotUseCase(timeSlot.toTimeSlot())
            navEvents.emit(NavigationEvent.NavigateToSettings.TimeSlotScreen(id.toString()))
        }
    }

    fun update(timeSlot: TimeSlotUiModel) {
        scope.launch {
            updateTimeSlotUseCase(timeSlot.toTimeSlot())
        }
    }

    fun delete(timeSlot: TimeSlotUiModel) {
        scope.launch {
            deleteTimeSlotUseCase(timeSlot.toTimeSlot())
        }
    }

    fun check(timeSlot: TimeSlotUiModel) : Boolean {
        return flow.value.timeSlots.filter { timeSlot.id != it.id}.all {
            var flag = true
            for (i in 0 until 7) {
                if (timeSlot.daysOfWeek[i] && it.daysOfWeek[i]) {
                    if (timeSlot.startTime < it.startTime) {
                        if (timeSlot.endTime > it.startTime) {
                            flag = false
                            break
                        }
                    } else if (timeSlot.startTime > it.startTime){
                        if (it.endTime > timeSlot.startTime) {
                            flag = false
                            break
                        }
                    } else {
                        flag = false
                        break
                    }
                }
            }
            flag
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(
            scope: CoroutineScope,
            flow: StateFlow<UiState.State>,
            navEvents: MutableSharedFlow<NavigationEvent>,
        ): TimeSlotFeatures
    }
}