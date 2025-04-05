package com.spksh.todoline.ui.model

data class Statistics(
    val totalTasksDone: Int = 0,
    val totalProgressMinutes: Int = 0,

    val totalQuadrant1TasksDone: Int = 0,
    val totalQuadrant2TasksDone: Int = 0,
    val totalQuadrant3TasksDone: Int = 0,
    val totalQuadrant4TasksDone: Int = 0,
    val totalQuadrant1ProgressMinutes: Int = 0,
    val totalQuadrant2ProgressMinutes: Int = 0,
    val totalQuadrant3ProgressMinutes: Int = 0,
    val totalQuadrant4ProgressMinutes: Int = 0,

    val tagsTotalTasksDone: List<Pair<Long, Int>> = emptyList(),
    val tagsTotalProgressMinutes: List<Pair<Long, Int>> = emptyList()
)
