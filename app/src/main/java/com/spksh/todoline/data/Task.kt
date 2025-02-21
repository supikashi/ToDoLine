package com.spksh.todoline.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "task_table")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String = "",
    val description: String = "",
    val importance: Int = 10,
    val urgency: Int = 10,
    val deadline: Long? = null,
    val requiredTime: Int = 0,
    val tagsIds: List<Int> = emptyList(),
    val parentTaskId: Int? = null,
    val childTasksIds: List<Int> = emptyList(),
    val progress: Float = 0f,
)
