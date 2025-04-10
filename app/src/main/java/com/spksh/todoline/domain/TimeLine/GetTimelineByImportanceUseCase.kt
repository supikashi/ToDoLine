package com.spksh.todoline.domain.TimeLine

import com.spksh.todoline.ui.model.ActivityUiModel
import com.spksh.todoline.ui.model.TaskUiModel
import com.spksh.todoline.ui.model.TimeSlotUiModel
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject

class GetTimelineByImportanceUseCase @Inject constructor(
    private val getTimelineUseCase: GetTimelineUseCase,
) {
    suspend operator fun invoke(
        _tasks: List<TaskUiModel>,
        activities: List<ActivityUiModel>,
        timeSlots: List<TimeSlotUiModel>,
        zoneId: ZoneId
    ) : List<TaskUiModel>? {
        val tasks = _tasks.toMutableList()
        while (true) {
            val tmp = getTimelineUseCase(tasks, false, activities, timeSlots, zoneId) ?: return null
            val overdueTasks = tmp
                .filter { it.importance > 5 }
                .sortedBy {  it.deadlineLocal ?: if (it.urgency < 6) LocalDateTime.MAX else LocalDateTime.MIN }
            var flag = false
            for (overdueTask in overdueTasks) {
                var lastUnimportantTaskIndex: Int? = null
                for (i in 0 until tasks.size) {
                    if (tasks[i].id == overdueTask.id) {
                        lastUnimportantTaskIndex?.let {
                            for (j in it until i) {
                                tasks[j] = tasks[j + 1].also { tasks[j + 1] = tasks[j] }
                            }
                            flag = true
                        }
                    } else if (tasks[i].importance < 6) {
                        lastUnimportantTaskIndex = i
                    }
                    if (flag) {
                        break
                    }
                }
                if (flag) {
                    break
                }
            }
            if (flag.not()) {
                break
            }
        }
        return getTimelineUseCase(tasks, true, activities, timeSlots, zoneId)
    }
}