package com.spksh.todoline.data.Event

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "event_table")
data class Event(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String = "",
    val description: String = "",
    val startTime: Long = 0,
    val endTime: Long = 0,
)
