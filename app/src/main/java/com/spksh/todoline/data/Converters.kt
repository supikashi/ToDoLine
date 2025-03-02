package com.spksh.todoline.data

import androidx.room.TypeConverter
import java.time.Instant

class Converters {
    @TypeConverter
    fun fromList(list: List<Long>): String {
        return list.joinToString(",")
    }

    @TypeConverter
    fun toList(data: String): List<Long> {
        return if (data.isEmpty()) emptyList() else data.split(",").map { it.toLong() }
    }
}