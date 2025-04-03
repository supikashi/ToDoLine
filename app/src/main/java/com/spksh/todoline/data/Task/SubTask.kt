package com.spksh.todoline.data.Task

import kotlinx.serialization.Serializable

@Serializable
data class SubTask(
    val id: Long = 0,
    val name: String = "",
    val requiredTime: Int = 60,
    val progress: Int = 0
)