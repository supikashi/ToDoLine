package com.spksh.todoline.ui.model

import com.spksh.todoline.data.Task.Task
import java.time.LocalDate
import java.time.LocalDateTime

data class TaskUiModel(
    val task: Task = Task(),
    val deadlineLocal: LocalDateTime? = null,
    val deadlineText: String? = null,
    val progressDatesLocal: List<Pair<LocalDateTime, Int>> = emptyList()
) : TimeAware {
    override fun getDay(): LocalDate? {
        return deadlineLocal?.toLocalDate()
    }

}