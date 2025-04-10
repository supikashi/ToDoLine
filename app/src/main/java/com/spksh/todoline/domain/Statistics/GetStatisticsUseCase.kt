package com.spksh.todoline.domain.Statistics

import com.spksh.todoline.data.Event.EventRepository
import com.spksh.todoline.data.Tag.Tag
import com.spksh.todoline.ui.model.Statistics
import com.spksh.todoline.ui.model.TaskUiModel
import java.time.LocalDate
import javax.inject.Inject

class GetStatisticsUseCase @Inject constructor(
    private val eventRepository: EventRepository
) {
    suspend operator fun invoke(
        startDate: LocalDate,
        endDate: LocalDate,
        tags: List<Tag>,
        tasks: List<TaskUiModel>
    ) : Statistics {
        var totalTasksDone = 0
        var totalQuadrant1TasksDone = 0
        var totalQuadrant2TasksDone = 0
        var totalQuadrant3TasksDone = 0
        var totalQuadrant4TasksDone = 0
        val tagsTotalTasksDone: MutableMap<Long, Int> = mutableMapOf()
        tags.forEach {
            tagsTotalTasksDone[it.id] = 0
        }

        tasks
            .filter { it.progress == it.requiredTime }
            .filter { it.progressDatesLocal.last().first.toLocalDate().between(startDate, endDate) }
            .forEach {
                totalTasksDone++
                if (it.importance >= 6 && it.urgency >= 6) {
                    totalQuadrant1TasksDone++
                } else if (it.importance >= 6 && it.urgency <= 5) {
                    totalQuadrant2TasksDone++
                } else if (it.importance <= 5 && it.urgency >= 6) {
                    totalQuadrant3TasksDone++
                } else {
                    totalQuadrant4TasksDone++
                }
                it.tagsIds.forEach {
                    tagsTotalTasksDone[it] = tagsTotalTasksDone[it]?.plus(1) ?: 1
                }
            }

        var totalProgressMinutes = 0
        var totalQuadrant1ProgressMinutes = 0
        var totalQuadrant2ProgressMinutes = 0
        var totalQuadrant3ProgressMinutes = 0
        var totalQuadrant4ProgressMinutes = 0
        val tagsTotalProgressMinutes: MutableMap<Long, Int> = mutableMapOf()
        tags.forEach {
            tagsTotalProgressMinutes[it.id] = 0
        }

        tasks.forEach { task ->
            task.progressDatesLocal.forEach { progress ->
                if (progress.first.toLocalDate().between(startDate, endDate)) {
                    totalProgressMinutes += progress.second
                    if (task.importance >= 6 && task.urgency >= 6) {
                        totalQuadrant1ProgressMinutes += progress.second
                    } else if (task.importance >= 6 && task.urgency <= 5) {
                        totalQuadrant2ProgressMinutes += progress.second
                    } else if (task.importance <= 5 && task.urgency >= 6) {
                        totalQuadrant3ProgressMinutes += progress.second
                    } else {
                        totalQuadrant4ProgressMinutes += progress.second
                    }
                    task.tagsIds.forEach {
                        tagsTotalProgressMinutes[it] = tagsTotalProgressMinutes[it]?.plus(progress.second) ?: progress.second
                    }
                }
            }
        }
        return Statistics(
            totalTasksDone = totalTasksDone,
            totalProgressMinutes = totalProgressMinutes,
            totalQuadrant1TasksDone = totalQuadrant1TasksDone,
            totalQuadrant2TasksDone = totalQuadrant2TasksDone,
            totalQuadrant3TasksDone = totalQuadrant3TasksDone,
            totalQuadrant4TasksDone = totalQuadrant4TasksDone,
            totalQuadrant1ProgressMinutes = totalQuadrant1ProgressMinutes,
            totalQuadrant2ProgressMinutes = totalQuadrant2ProgressMinutes,
            totalQuadrant3ProgressMinutes = totalQuadrant3ProgressMinutes,
            totalQuadrant4ProgressMinutes = totalQuadrant4ProgressMinutes,
            tagsTotalTasksDone = tagsTotalTasksDone.toList(),
            tagsTotalProgressMinutes = tagsTotalProgressMinutes.toList()
        )
    }

    private fun LocalDate.between(startDate: LocalDate, endDate: LocalDate) : Boolean {
        return (startDate.isAfter(this) || endDate.isBefore(this)).not()
    }
}