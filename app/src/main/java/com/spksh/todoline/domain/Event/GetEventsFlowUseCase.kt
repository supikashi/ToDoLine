package com.spksh.todoline.domain.Event

import com.spksh.todoline.data.Event.Event
import com.spksh.todoline.data.Event.EventRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetEventsFlowUseCase @Inject constructor(
    private val eventRepository: EventRepository
) {
    operator fun invoke(): Flow<List<Event>> {
        return eventRepository.allItemsFlow
    }
}