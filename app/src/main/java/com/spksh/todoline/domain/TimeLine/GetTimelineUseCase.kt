package com.spksh.todoline.domain.TimeLine

import com.spksh.todoline.domain.Task.ChangeTasksOrderUseCase
import com.spksh.todoline.domain.TimelinedActivity.AddAllActivitiesUseCase
import com.spksh.todoline.domain.TimelinedActivity.DeleteAllTasksUseCase
import com.spksh.todoline.ui.model.ActivityUiModel
import com.spksh.todoline.ui.model.TaskUiModel
import com.spksh.todoline.ui.model.TimeSlotUiModel
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import javax.inject.Inject

class GetTimelineUseCase @Inject constructor(
    private val changeTasksOrderUseCase: ChangeTasksOrderUseCase,
    private val deleteAllTasksUseCase: DeleteAllTasksUseCase,
    private val addAllActivitiesUseCase: AddAllActivitiesUseCase
) {
    suspend operator fun invoke(
        tasklist: List<TaskUiModel>,
        saveTimeline: Boolean = true,
        activities: List<ActivityUiModel>,
        timeSlots: List<TimeSlotUiModel>,
        zoneId: ZoneId
    ) : List<TaskUiModel>? {
        val startTime = LocalDateTime.now().withNano(0).withSecond(0)
        val eventActivities = activities
            .filter { !it.isTask }
            .filter { it.endTimeLocal.isAfter(startTime) }
        val tasks = tasklist
            .filter { it.progress != it.requiredTime }
            .flatMap {
                it.subTasks.mapNotNull { subTask ->
                    if (subTask.requiredTime != subTask.progress) {
                        Pair(it.copy(
                            requiredTime = subTask.requiredTime,
                            progress = subTask.progress
                        ), subTask.id)
                    } else {
                        null
                    }
                }.plus(Pair(it, 0L))
            }
            .map {
                it.copy(
                    first = it.first.copy(
                        requiredTime = (it.first.requiredTime - it.first.progress)
                    )
                )
            }
        var taskIndex = 0
        val tmpActivityList: MutableList<MutableList<ActivityUiModel>> = mutableListOf()
        val tmpTasks: MutableList<Pair<Pair<TaskUiModel, Long>, Int>> = mutableListOf()
        val usedTasks = MutableList(tasks.size) { false }
        var currentDate = startTime.toLocalDate()
        var freeTime = listOf<FreeTime>()
        var eventIndex = 0
        val timelinedTasks = mutableListOf<ActivityUiModel>()
        var f = getFreeTime(startTime, timeSlots, eventActivities, eventIndex)
        eventIndex = f.second
        freeTime = f.first

        var prevTaskIndex = taskIndex
        var prevTmpTasks = tmpTasks
        var cnt = 0

        while (taskIndex != tasks.size || tmpTasks.isNotEmpty()) {
            taskIndex = addTimelinedTasks(timelinedTasks, tmpActivityList, tmpTasks, freeTime, tasks, usedTasks, taskIndex)
            if (prevTaskIndex == taskIndex) {
                cnt++
            } else {
                cnt = 0
            }
            prevTaskIndex = taskIndex
            currentDate = currentDate.plusDays(1)
            f = getFreeTime(currentDate.atStartOfDay(), timeSlots, eventActivities, eventIndex)
            eventIndex = f.second
            freeTime = f.first
            if (cnt > 8) {
                return null
            }
        }
        val overdueTasks = mutableMapOf<Long, Boolean>()
        timelinedTasks.forEach {
            if (it.subtaskId == 0L && it.partIndex == it.numberOfParts) {
                overdueTasks[it.activityId] = it.isDeadlineMet
            }
        }
        if (saveTimeline) {
            changeTasksOrderUseCase(tasklist.filter { it.requiredTime != it.progress }.map {it.toTask()})
            deleteAllTasksUseCase()
            addAllActivitiesUseCase(
                timelinedTasks.map {
                    it.copy(isDeadlineMet = overdueTasks.getValue(it.activityId))
                }.map {it.toActivity(zoneId) }
            )
        }
        return timelinedTasks
            .asSequence()
            .filter { it.isTask && !it.isDeadlineMet }
            .map { it.activityId }.toSet().toList()
            .mapNotNull { id -> tasklist.find {it.id == id} }
            .toList()
    }

    private fun addTimelinedTasks(
        timelinedTasks: MutableList<ActivityUiModel>,
        tmpActivityList: MutableList<MutableList<ActivityUiModel>>,
        tmpTasks: MutableList<Pair<Pair<TaskUiModel, Long>, Int>>,
        freeTime: List<FreeTime>,
        tasks: List<Pair<TaskUiModel,Long>>,
        usedTasks: MutableList<Boolean>,
        startIndex: Int
    ) : Int {
        var taskIndex = startIndex

        for (time in freeTime) {
            var tmpStart = time.startTime
            if (taskIndex == usedTasks.size && tmpTasks.isEmpty()) {
                break
            }
            while (tmpStart.isBefore(time.endTime)) {
                while (taskIndex != usedTasks.size && usedTasks[taskIndex]) {
                    taskIndex++
                }
                var suitableTaskIndex: Int? = null
                for (i in taskIndex until tasks.size) {
                    if (!usedTasks[i] && (time.tagId == 0L || time.tagId in tasks[i].first.tagsIds)) {
                        suitableTaskIndex = i
                        break
                    }
                }
                var suitableTmpTaskIndex: Int? = null
                for (i in 0 until tmpTasks.size) {
                    if (suitableTaskIndex == null || suitableTaskIndex > tmpTasks[i].second) {
                        if (time.tagId == 0L || time.tagId in tmpTasks[i].first.first.tagsIds) {
                            if (suitableTmpTaskIndex == null || tmpTasks[suitableTmpTaskIndex].second > tmpTasks[i].second) {
                                suitableTmpTaskIndex = i
                            }
                        }
                    }
                }

                if (suitableTmpTaskIndex != null)  {
                    val i = suitableTmpTaskIndex
                    val tmpTask = tmpTasks[i].first
                    val tmpEnd = tmpStart.plusMinutes(tmpTask.first.requiredTime.toLong())
                    if (tmpEnd.isAfter(time.endTime)) {
                        tmpActivityList[i].add(
                            ActivityUiModel(
                                isTask = true,
                                activityId = tmpTask.first.id,
                                subtaskId = tmpTask.second,
                                startTimeLocal = tmpStart,
                                endTimeLocal = time.endTime,
                            )
                        )
                        val timeRemaining = tmpTask.first.requiredTime - ChronoUnit.MINUTES.between(tmpStart, time.endTime).toInt()
                        tmpTasks[i] = Pair(tmpTask.copy(first = tmpTask.first.copy(requiredTime = timeRemaining)), tmpTasks[i].second)
                        tmpStart = time.endTime
                    } else {
                        tmpActivityList[i].add(
                            ActivityUiModel(
                                isTask = true,
                                activityId = tmpTask.first.id,
                                subtaskId = tmpTask.second,
                                startTimeLocal = tmpStart,
                                endTimeLocal = tmpEnd,
                            )
                        )
                        timelinedTasks.addAll(tmpActivityList[i].mapIndexed { index, activity ->
                            activity.copy(
                                numberOfParts = tmpActivityList[i].size,
                                partIndex = index + 1,
                                isDeadlineMet = tmpTask.first.deadlineLocal?.let {!it.isBefore(tmpEnd)} ?: true
                            )
                        })
                        tmpTasks.removeAt(i)
                        tmpActivityList.removeAt(i)
                        tmpStart = tmpEnd
                    }
                } else if (suitableTaskIndex != null) {
                    val i = suitableTaskIndex
                    usedTasks[i] = true
                    val tmpEnd = tmpStart.plusMinutes(tasks[i].first.requiredTime.toLong())
                    if (tmpEnd.isAfter(time.endTime)) {
                        tmpActivityList.add(mutableListOf(ActivityUiModel(
                            isTask = true,
                            activityId = tasks[i].first.id,
                            subtaskId = tasks[i].second,
                            startTimeLocal = tmpStart,
                            endTimeLocal = time.endTime,
                        )))
                        val timeRemaining = tasks[i].first.requiredTime - ChronoUnit.MINUTES.between(tmpStart, time.endTime).toInt()
                        tmpTasks.add(Pair(tasks[i].copy(first = tasks[i].first.copy(requiredTime = timeRemaining)), i))
                        tmpStart = time.endTime
                    } else {
                        timelinedTasks.add(
                            ActivityUiModel(
                                isTask = true,
                                activityId = tasks[i].first.id,
                                subtaskId = tasks[i].second,
                                startTimeLocal = tmpStart,
                                endTimeLocal = tmpEnd,
                                isDeadlineMet = tasks[i].first.deadlineLocal?.let {!it.isBefore(tmpEnd)} ?: true,
                            )
                        )
                        tmpStart = tmpEnd
                    }
                } else {
                    tmpStart = time.endTime
                }
                if (taskIndex == usedTasks.size && tmpTasks.isEmpty()) {
                    break
                }
            }
        }
        return taskIndex
    }

    private fun getFreeTime(
        startTime: LocalDateTime,
        timeSlots: List<TimeSlotUiModel>,
        eventActivities: List<ActivityUiModel>,
        startIndex: Int
    ) : Pair<List<FreeTime>, Int> {
        var eventIndex = startIndex
        val currentDate = startTime.toLocalDate()
        val workingHours = timeSlots
            .filter { it.daysOfWeek[currentDate.dayOfWeek.value - 1] }
            .filter { it.endTime > startTime.hour * 60 + startTime.minute }
            .map {
                if (it.startTime < startTime.hour * 60 + startTime.minute) {
                    it.copy(startTime = startTime.hour * 60 + startTime.minute)
                } else {
                    it
                }
            }
            .map {
                FreeTime(
                    startTime = currentDate.atTime(it.startTime / 60, it.startTime % 60),
                    endTime = currentDate.atTime(it.endTime / 60, it.endTime % 60),
                    tagId = it.tagId
                )
            }
        val freeTime = mutableListOf<FreeTime>()
        workingHours.forEach { item ->
            var tmpItemStartTime = item.startTime
            while (tmpItemStartTime.isBefore(item.endTime)) {
                if (eventIndex == eventActivities.size) {
                    freeTime.add(FreeTime(tmpItemStartTime, item.endTime, item.tagId))
                    break
                }
                if (eventActivities[eventIndex].endTimeLocal.isAfter(tmpItemStartTime)) {
                    if (eventActivities[eventIndex].startTimeLocal.isAfter(tmpItemStartTime)) {
                        if (item.endTime.isBefore(eventActivities[eventIndex].startTimeLocal)) {
                            freeTime.add(FreeTime(tmpItemStartTime, item.endTime, item.tagId))
                        } else {
                            freeTime.add(FreeTime(tmpItemStartTime, eventActivities[eventIndex].startTimeLocal, item.tagId))
                        }
                    }
                    tmpItemStartTime = eventActivities[eventIndex].endTimeLocal
                    if (!tmpItemStartTime.isAfter(item.endTime)) {
                        eventIndex++
                    }
                } else {
                    eventIndex++
                }
            }
        }
        return Pair(freeTime, eventIndex)
    }

    private data class FreeTime(
        val startTime: LocalDateTime,
        val endTime: LocalDateTime,
        val tagId: Long
    )
}