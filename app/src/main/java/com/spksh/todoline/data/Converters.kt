package com.spksh.todoline.data

import androidx.room.TypeConverter
import com.spksh.todoline.data.Task.SubTask
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class Converters {
    @TypeConverter
    fun fromListOfLongs(list: List<Long>): String {
        return list.joinToString(",")
    }

    @TypeConverter
    fun toListOfLongs(data: String): List<Long> {
        return if (data.isEmpty()) emptyList() else data.split(",").map { it.toLong() }
    }

    @TypeConverter
    fun fromListOfBooleans(list: List<Boolean>): String {
        Boolean.toString()
        return list.map { if (it) 1 else 0 } .joinToString(",")
    }

    @TypeConverter
    fun toListOfBooleans(data: String): List<Boolean> {
        return if (data.isEmpty()) emptyList() else data.split(",").map { it == "1" }
    }

    @TypeConverter
    fun fromListOfSubTasks(list: List<SubTask>): String {
        return Json.encodeToString(list)
    }

    @TypeConverter
    fun toListOfSubTasks(data: String): List<SubTask> {
        return Json.decodeFromString<List<SubTask>>(data)
    }

    @TypeConverter
    fun fromListOfProgressDates(list: List<Pair<Long, Int>>) : String {
        return list.map {"${it.first},${it.second}"}.joinToString(";")
    }

    @TypeConverter
    fun toListOfSubProgressDates(data: String): List<Pair<Long, Int>> {
        return if (data.isEmpty()) emptyList() else data.split(';').map {
            val split = it.split(',')
            Pair(split[0].toLong(), split[1].toInt())
        }
    }
}