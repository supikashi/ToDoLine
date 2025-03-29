package com.spksh.todoline.ui.model

import java.time.LocalDateTime

data class ActivityUiModel(
    val id: Long = 0,
    val startTime: Long = 0,
    val endTime: Long = 0,
    val numberOfParts: Int = 1,
    val partIndex: Int = 1,
    val isTask: Boolean = false,
    val isDone: Boolean = false,
    val isDeadlineMet: Boolean = false,
    val activityId: Long = 0,
    val startTimeLocal: LocalDateTime = LocalDateTime.MIN,
    val endTimeLocal: LocalDateTime = LocalDateTime.MIN,
)
