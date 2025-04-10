package com.spksh.todoline.domain.TimelinedActivity

import android.util.Log
import com.spksh.todoline.data.Event.Event
import com.spksh.todoline.data.TimeLinedActivity.TimeLinedActivity
import com.spksh.todoline.ui.model.EventUiModel
import java.time.Instant
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import javax.inject.Inject

class GetActivitiesByEventUseCase @Inject constructor() {
    operator fun invoke(event: Event, zoneId: ZoneId) : List<TimeLinedActivity> {
        val list = mutableListOf<TimeLinedActivity>()
        var eventStart = Instant.ofEpochMilli(event.startTime).atZone(zoneId).toLocalDateTime()
        val startDate = eventStart.toLocalDate()
        val eventEnd = Instant.ofEpochMilli(event.endTime).atZone(zoneId).toLocalDateTime()
        val endDate = if (eventEnd.hour == 0 && eventEnd.minute == 0) {
            eventEnd.toLocalDate().minusDays(1)
        } else {
            eventEnd.toLocalDate()
        }
        if (ChronoUnit.MINUTES.between(eventStart, eventEnd) > 0) {
            val numberOfParts = ChronoUnit.DAYS.between(startDate, endDate).toInt() + 1
            var partIndex = 1
            while (eventStart.toLocalDate() != endDate) {
                list.add(
                    TimeLinedActivity(
                        startTime = eventStart.atZone(zoneId).toInstant().toEpochMilli(),
                        endTime = eventStart
                            .toLocalDate()
                            .plusDays(1)
                            .atStartOfDay()
                            .atZone(zoneId)
                            .toInstant()
                            .toEpochMilli(),
                        numberOfParts = numberOfParts,
                        partIndex = partIndex,
                        isTask = false,
                        activityId = event.id
                    )
                )
                eventStart = eventStart
                    .toLocalDate()
                    .plusDays(1)
                    .atStartOfDay()
                partIndex++
            }
            list.add(
                TimeLinedActivity(
                    startTime = eventStart.atZone(zoneId).toInstant().toEpochMilli(),
                    endTime = eventEnd.atZone(zoneId).toInstant().toEpochMilli(),
                    numberOfParts = numberOfParts,
                    partIndex = partIndex,
                    isTask = false,
                    activityId = event.id
                )
            )
        } else {
            Log.i("mytag", "event wrap error")
        }
        return list
    }
}