package com.spksh.todoline.ui.model

import com.spksh.todoline.data.Task.SubTask
import com.spksh.todoline.data.Task.Task
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

data class TaskUiModel(
    val id: Long = 0,
    val name: String = "",
    val description: String = "",
    val importance: Int = 10,
    val urgency: Int = 10,
    val deadline: Long? = null,
    val requiredTime: Int = 60,
    val tagsIds: List<Long> = emptyList(),
    val parentTaskId: Long? = null,
    val childTasksIds: List<Long> = emptyList(),
    val subTasks: List<SubTask> = emptyList(),
    val progress: Int = 0,
    val progressDates: List<Pair<Long, Int>> = emptyList(),
    val deadlineLocal: LocalDateTime? = null,
    val deadlineText: String? = null,
    val progressDatesLocal: List<Pair<LocalDateTime, Int>> = emptyList()
) {

    fun toTask(): Task {
        return Task(
            id = this.id,
            name = this.name,
            description = this.description,
            importance = this.importance,
            urgency = this.urgency,
            deadline = this.deadline,
            requiredTime = this.requiredTime,
            tagsIds = this.tagsIds,
            parentTaskId = this.parentTaskId,
            childTasksIds = this.childTasksIds,
            subTasks = this.subTasks,
            progress = this.progress,
            progressDates = this.progressDates,
        )
    }
    constructor(task: Task, zoneId: ZoneId) : this(
        id = task.id,
        name = task.name,
        description = task.description,
        importance = task.importance,
        urgency = task.urgency,
        deadline = task.deadline,
        requiredTime = task.requiredTime,
        tagsIds = task.tagsIds,
        parentTaskId = task.parentTaskId,
        childTasksIds = task.childTasksIds,
        subTasks = task.subTasks,
        progress = task.progress,
        progressDates = task.progressDates,
        deadlineLocal = task.deadline?.let {
            Instant.ofEpochMilli(it)
                .atZone(zoneId)
                .toLocalDateTime()
        },
        deadlineText = task.deadline?.let {
            Instant.ofEpochMilli(it)
                .atZone(zoneId)
                .toLocalDateTime()
        }?.format(DateTimeFormatter.ofPattern("MMM d yyyy H:mm")),
        progressDatesLocal = task.progressDates.map {
            Pair(Instant.ofEpochMilli(it.first).atZone(zoneId).toLocalDateTime(), it.second)
        }
    )
}