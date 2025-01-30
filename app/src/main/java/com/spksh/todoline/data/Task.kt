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
    val isDone: Boolean = false
)
