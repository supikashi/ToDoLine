package com.spksh.todoline.data

import androidx.room.TypeConverter

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
}