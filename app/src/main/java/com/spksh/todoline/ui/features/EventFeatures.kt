package com.spksh.todoline.ui.features

import com.spksh.todoline.domain.Event.AddEventUseCase
import com.spksh.todoline.domain.Event.DeleteEventUseCase
import com.spksh.todoline.domain.Event.UpdateEventUseCase
import com.spksh.todoline.ui.MainViewModel.NavigationEvent
import com.spksh.todoline.ui.model.EventUiModel
import com.spksh.todoline.ui.model.UiState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.ZoneId

class EventFeatures @AssistedInject constructor(
    private val addEventUseCase: AddEventUseCase,
    private val updateEventUseCase: UpdateEventUseCase,
    private val deleteEventUseCase: DeleteEventUseCase,
    @Assisted private val scope: CoroutineScope,
    @Assisted private val flow: StateFlow<UiState.State>,
    @Assisted private val navEvents: MutableSharedFlow<NavigationEvent>,
    @Assisted private val zoneId: ZoneId
) {
    fun add(event: EventUiModel) {
        scope.launch {
            val id = addEventUseCase(event.toEvent(), zoneId)
            navEvents.emit(NavigationEvent.NavigateToEventScreen(id.toString()))
        }
    }

    fun update(event: EventUiModel) {
        scope.launch {
            val oldEvent = flow.value.events.find { it.id == event.id }
            updateEventUseCase(event.toEvent(), oldEvent?.toEvent(), zoneId)
        }
    }

    fun delete(event: EventUiModel) {
        scope.launch {
            deleteEventUseCase(event.toEvent())
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(
            scope: CoroutineScope,
            flow: StateFlow<UiState.State>,
            navEvents: MutableSharedFlow<NavigationEvent>,
            zoneId: ZoneId
        ): EventFeatures
    }
}