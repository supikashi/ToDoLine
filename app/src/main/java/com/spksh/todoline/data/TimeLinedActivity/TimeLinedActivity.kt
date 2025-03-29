package com.spksh.todoline.data.TimeLinedActivity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "activity_table")
data class TimeLinedActivity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val startTime: Long = 0,
    val endTime: Long = 0,
    val numberOfParts: Int = 1,
    val partIndex: Int = 1,
    val isTask: Boolean = false,
    val isDone: Boolean = false,
    val isDeadlineMet: Boolean = true,
    val activityId: Long = 0,
)
