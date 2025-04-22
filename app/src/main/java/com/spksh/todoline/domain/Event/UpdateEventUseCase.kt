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
        oldEvent?.let {
            var correctEvent = event
            if (event.startTime != it.startTime) {
                if (event.startTime >= event.endTime) {
                    correctEvent = correctEvent.copy(endTime = event.startTime + it.endTime - it.startTime)
                }
            }
            if (event.endTime != it.endTime) {
                if (event.startTime >= event.endTime) {
                    correctEvent = correctEvent.copy(startTime = event.endTime - it.endTime + it.startTime)
                }
            }
            eventRepository.update(correctEvent)
            if (it.startTime != correctEvent.startTime || it.endTime != correctEvent.endTime) {
                deleteAllByIdUseCase(correctEvent.id, false)
                addAllActivitiesUseCase(getActivitiesByEventUseCase(correctEvent, zoneId))
            }
        }
    }
}