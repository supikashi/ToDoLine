package com.spksh.todoline.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.spksh.todoline.data.Event.Event
import com.spksh.todoline.data.Event.EventDao
import com.spksh.todoline.data.Tag.Tag
import com.spksh.todoline.data.Tag.TagDao
import com.spksh.todoline.data.Task.Task
import com.spksh.todoline.data.Task.TaskDao
import com.spksh.todoline.data.TimeLinedActivity.TimeLinedActivity
import com.spksh.todoline.data.TimeLinedActivity.TimeLinedActivityDao
import com.spksh.todoline.data.TimeSlot.TimeSlot
import com.spksh.todoline.data.TimeSlot.TimeSlotDao

@Database(entities = [Task::class, Tag::class, Event::class, TimeLinedActivity::class, TimeSlot::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun tagDao(): TagDao
    abstract fun eventDao(): EventDao
    abstract fun timeLinedActivityDao(): TimeLinedActivityDao
    abstract fun timeSlotDao(): TimeSlotDao
}