package com.spksh.todoline.data.Task

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "task_table")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String = "",
    val description: String = "",
    val importance: Int = 10,
    val urgency: Int = 10,
    val deadline: Long? = null,
    val requiredTime: Int = 60,
    val tagsIds: List<Long> = emptyList(),
    val parentTaskId: Long? = null,
    val childTasksIds: List<Long> = emptyList(),
    val progress: Int = 0,
)
