package com.spksh.todoline.data.TimeSlot

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "timeslot_table")
data class TimeSlot(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val tagId: Long = 0,
    val daysOfWeek: List<Boolean> = List(7) { true },
    val startTime: Int = 0,
    val endTime:Int = 0,
)