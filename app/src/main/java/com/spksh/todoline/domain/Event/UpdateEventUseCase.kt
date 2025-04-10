package com.spksh.todoline.domain.Event

import com.spksh.todoline.data.Event.Event
import com.spksh.todoline.data.Event.EventRepository
import com.spksh.todoline.domain.TimelinedActivity.AddAllActivitiesUseCase
import com.spksh.todoline.domain.TimelinedActivity.DeleteAllByIdUseCase
import com.spksh.todoline.domain.TimelinedActivity.GetActivitiesByEventUseCase
import java.time.ZoneId
import javax.inject.Inject

class UpdateEventUseCase @Inject constructor(
    private val eventRepository: EventRepository,
    private val getActivitiesByEventUseCase: GetActivitiesByEventUseCase,
    private val addAllActivitiesUseCase: AddAllActivitiesUseCase,
    private val deleteAllByIdUseCase: DeleteAllByIdUseCase
) {
    suspend operator fun invoke(event: Event, oldEvent: Event?, zoneId: ZoneId) {
        if (event.startTime < event.endTime) {
            oldEvent?.let {
                eventRepository.update(event)
                if (it.startTime != event.startTime || it.endTime != event.endTime) {
                    deleteAllByIdUseCase(event.id, false)
                    addAllActivitiesUseCase(getActivitiesByEventUseCase(event, zoneId))
                }
            }
        }
    }
}