package com.spksh.todoline.ui.model

import java.time.LocalDateTime

data class EventUiModel(
    val id: Long = 0,
    val name: String = "",
    val description: String = "",
    val startTime: Long = 0,
    val endTime: Long = 0,
    val startTimeLocal: LocalDateTime = LocalDateTime.now(),
    val endTimeLocal: LocalDateTime = LocalDateTime.now(),
)