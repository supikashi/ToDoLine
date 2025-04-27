package com.spksh.todoline.domain.Event

import com.spksh.todoline.data.Event.Event
import com.spksh.todoline.data.Event.EventRepository
import com.spksh.todoline.domain.TimelinedActivity.AddAllActivitiesUseCase
import com.spksh.todoline.domain.TimelinedActivity.GetActivitiesByEventUseCase
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject

class AddEventUseCase @Inject constructor(
    private val eventRepository: EventRepository,
    private val getActivitiesByEventUseCase: GetActivitiesByEventUseCase,
    private val addAllActivitiesUseCase: AddAllActivitiesUseCase,
) {
    suspend operator fun invoke(event: Event, zoneId: ZoneId): Long {
        val correctedEvent = if (event.startTime == 0L || event.endTime == 0L) {
            event.copy(
                startTime = LocalDateTime.now()
                    .withNano(0)
                    .withSecond(0)
                    .atZone(zoneId)
                    .toInstant()
                    .toEpochMilli(),
                endTime = LocalDateTime.now()
                    .withNano(0)
                    .withSecond(0)
                    .plusHours(1)
                    .atZone(zoneId)
                    .toInstant()
                    .toEpochMilli()
            )
        } else {
            event
        }
        val id = eventRepository.insert(correctedEvent)
        addAllActivitiesUseCase(getActivitiesByEventUseCase(correctedEvent.copy(id = id), zoneId))
        return id
    }
}