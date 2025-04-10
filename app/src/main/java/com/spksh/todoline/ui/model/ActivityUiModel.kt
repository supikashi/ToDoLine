package com.spksh.todoline.ui.model

import com.spksh.todoline.data.TimeLinedActivity.TimeLinedActivity
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

data class ActivityUiModel(
    val id: Long = 0,
    val startTime: Long = 0,
    val endTime: Long = 0,
    val numberOfParts: Int = 1,
    val partIndex: Int = 1,
    val activityId: Long = 0,
    val isTask: Boolean = false,
    val isDone: Boolean = false,
    val isDeadlineMet: Boolean = false,
    val subtaskId: Long = 0,
    val startTimeLocal: LocalDateTime = LocalDateTime.MIN,
    val endTimeLocal: LocalDateTime = LocalDateTime.MIN,
) {
    fun toActivity(zoneId: ZoneId): TimeLinedActivity {
        return TimeLinedActivity(
            id = this.id,
            startTime = this.startTimeLocal.atZone(zoneId).toInstant().toEpochMilli(),
            endTime = this.endTimeLocal.atZone(zoneId).toInstant().toEpochMilli(),
            numberOfParts = this.numberOfParts,
            partIndex = this.partIndex,
            activityId = this.activityId,
            isTask = this.isTask,
            isDone = this.isDone,
            isDeadlineMet = this.isDeadlineMet,
            subtaskId = this.subtaskId,
        )
    }
    constructor(timeLinedActivity: TimeLinedActivity, zoneId: ZoneId) : this(
        id = timeLinedActivity.id,
        startTime = timeLinedActivity.startTime,
        endTime = timeLinedActivity.endTime,
        numberOfParts = timeLinedActivity.numberOfParts,
        partIndex = timeLinedActivity.partIndex,
        activityId = timeLinedActivity.activityId,
        isTask = timeLinedActivity.isTask,
        isDone = timeLinedActivity.isDone,
        isDeadlineMet = timeLinedActivity.isDeadlineMet,
        subtaskId = timeLinedActivity.subtaskId,
        startTimeLocal = Instant.ofEpochMilli(timeLinedActivity.startTime)
            .atZone(zoneId)
            .toLocalDateTime(),
        endTimeLocal = Instant.ofEpochMilli(timeLinedActivity.endTime)
            .atZone(zoneId)
            .toLocalDateTime()
    )
}
