package com.spksh.todoline.domain.Event

import com.spksh.todoline.data.Event.Event
import com.spksh.todoline.data.Event.EventRepository
import javax.inject.Inject

class DeleteEventUseCase @Inject constructor(
    private val eventRepository: EventRepository
) {
    suspend operator fun invoke(event: Event) {
        eventRepository.delete(event)
    }
}