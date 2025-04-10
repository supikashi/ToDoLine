package com.spksh.todoline.ui.model

import com.spksh.todoline.data.TimeSlot.TimeSlot

data class TimeSlotUiModel(
    val id: Long = 0,
    val tagId: Long = 0,
    val daysOfWeek: List<Boolean> = List(7) { true },
    val startTime: Int = 0,
    val endTime: Int = 0,
) {
    fun toTimeSlot(): TimeSlot {
        return TimeSlot(
            id = this.id,
            tagId = this.tagId,
            daysOfWeek = this.daysOfWeek,
            startTime = this.startTime,
            endTime = this.endTime,
        )
    }
    constructor(timeSlot: TimeSlot) : this(
        id = timeSlot.id,
        tagId = timeSlot.tagId,
        daysOfWeek = timeSlot.daysOfWeek,
        startTime = timeSlot.startTime,
        endTime = timeSlot.endTime,
    )
}