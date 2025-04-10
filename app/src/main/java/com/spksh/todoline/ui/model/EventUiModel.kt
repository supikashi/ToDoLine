package com.spksh.todoline.ui.model

import com.spksh.todoline.data.Event.Event
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

data class EventUiModel(
    val id: Long = 0,
    val name: String = "",
    val description: String = "",
    val startTime: Long = 0,
    val endTime: Long = 0,
    val startTimeLocal: LocalDateTime = LocalDateTime.now(),
    val endTimeLocal: LocalDateTime = LocalDateTime.now(),
) {
    fun toEvent(): Event {
        return Event(
            id = this.id,
            name = this.name,
            description = this.description,
            startTime = this.startTime,
            endTime = this.endTime,
        )
    }
    constructor(event: Event, zoneId: ZoneId) : this(
        id = event.id,
        name = event.name,
        description = event.description,
        startTime = event.startTime,
        endTime = event.endTime,
        startTimeLocal = Instant.ofEpochMilli(event.startTime)
            .atZone(zoneId)
            .toLocalDateTime(),
        endTimeLocal = Instant.ofEpochMilli(event.endTime)
            .atZone(zoneId)
            .toLocalDateTime()
    )
}