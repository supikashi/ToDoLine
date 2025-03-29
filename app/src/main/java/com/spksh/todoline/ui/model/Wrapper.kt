package com.spksh.todoline.ui.model

import java.time.LocalDate
import java.time.LocalDateTime

sealed class Wrapper(
    val startTimeLocal: LocalDateTime = LocalDateTime.now(),
    val endTimeLocal: LocalDateTime = LocalDateTime.now(),
    val numberOfParts: Int = 1,
    val partIndex: Int = 1
) : TimeAware {
    override fun getDay(): LocalDate {
        return startTimeLocal.toLocalDate()
    }
}

class EventWrapper(
    startTimeLocal: LocalDateTime = LocalDateTime.now(),
    endTimeLocal: LocalDateTime = LocalDateTime.now(),
    numberOfParts: Int = 1,
    partIndex: Int = 1,
    val event: EventUiModel = EventUiModel(),
) : Wrapper(
    startTimeLocal = startTimeLocal,
    endTimeLocal = endTimeLocal,
    numberOfParts = numberOfParts,
    partIndex = partIndex
)

class TaskWrapper(
    startTimeLocal: LocalDateTime = LocalDateTime.now(),
    endTimeLocal: LocalDateTime = LocalDateTime.now(),
    numberOfParts: Int = 1,
    partIndex: Int = 1,
    val task: TaskUiModel = TaskUiModel(),
) : Wrapper(
    startTimeLocal = startTimeLocal,
    endTimeLocal = endTimeLocal,
    numberOfParts = numberOfParts,
    partIndex = partIndex
)

