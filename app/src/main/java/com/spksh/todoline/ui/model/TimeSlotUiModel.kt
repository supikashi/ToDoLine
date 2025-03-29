package com.spksh.todoline.ui.model

data class TimeSlotUiModel(
    val id: Long = 0,
    val tagId: Long = 0,
    val daysOfWeek: List<Boolean> = List(7) { true },
    val startTime: Int = 0,
    val endTime: Int = 0,
)