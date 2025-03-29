package com.spksh.todoline.data.Tag

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tag_table")
data class Tag(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String = "",
    val color: String = "#F5F5DC",
    val show: Boolean = true,
)