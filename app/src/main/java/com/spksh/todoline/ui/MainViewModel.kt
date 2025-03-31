package com.spksh.todoline.ui

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spksh.todoline.data.Event.Event
import com.spksh.todoline.data.Event.EventRepository
import com.spksh.todoline.data.TimeSlot.TimeSlotRepository
import com.spksh.todoline.data.Tag.Tag
import com.spksh.todoline.data.Task.Task
import com.spksh.todoline.data.Tag.TagRepository
import com.spksh.todoline.data.Task.TaskRepository
import com.spksh.todoline.data.TimeLinedActivity.TimeLinedActivity
import com.spksh.todoline.data.TimeLinedActivity.TimeLinedActivityRepository
import com.spksh.todoline.data.TimeSlot.TimeSlot
import com.spksh.todoline.ui.model.ActivityUiModel
import com.spksh.todoline.ui.model.EventUiModel
import com.spksh.todoline.ui.model.TaskUiModel
import com.spksh.todoline.ui.model.TimeSlotUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val tagRepository: TagRepository,
    private val eventRepository: EventRepository,
    private val timeLinedActivityRepository: TimeLinedActivityRepository,
    private val timeSlotRepository: TimeSlotRepository,
    //private val dataStoreRepository: DataStoreRepository
) : ViewModel() {

    private val _navigationEvents = MutableSharedFlow<NavigationEvent>()
    val navigationEvents = _navigationEvents.asSharedFlow()

    val uiState = combine(
        taskRepository.allItemsFlow,
        tagRepository.allItemsFlow,
        eventRepository.allItemsFlow,
        timeLinedActivityRepository.allItemsFlow,
        timeSlotRepository.allItemsFlow
    ) { tasks, tags, events, activities, timeSlots ->
        UiState.State(
            tasks.map { it.toUiModel() }.sortedBy {  it.deadlineLocal ?: if (it.task.urgency < 6) LocalDateTime.MAX else LocalDateTime.MIN },
            tags,
            events.map { it.toUiModel() }.sortedBy { it.endTime }.sortedBy { it.startTime },
            activities.map {it.toUiModel()}.sortedBy { it.endTime }.sortedBy { it.startTime },
            timeSlots.map {it.toUiModel()}.sortedBy { it.startTime },
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        UiState.State(
            emptyList(),
            emptyList(),
            emptyList(),
            emptyList(),
            emptyList(),
        )
    )

    sealed class UiState {
        //object Loading : UiState()
        data class State(
            val tasks: List<TaskUiModel>,
            val tags: List<Tag>,
            val events: List<EventUiModel>,
            val activities: List<ActivityUiModel>,
            val timeSlots: List<TimeSlotUiModel>
        ) : UiState() {
            val tasks_1 = tasks//.filter {it.task.progress != 1f }
                    .filter {it.task.importance >= 6 && it.task.urgency >= 6}.filterTags()
            val tasks_2 = tasks//.filter {it.task.progress != 1f }
                    .filter {it.task.importance >= 6 && it.task.urgency <= 5}.filterTags()
            val tasks_3 = tasks//.filter {it.task.progress != 1f }
                    .filter {it.task.importance <= 5 && it.task.urgency >= 6}.filterTags()
            val tasks_4 = tasks//.filter {it.task.progress != 1f }
                    .filter {it.task.importance <= 5 && it.task.urgency <= 5}.filterTags()

            val currentTime: LocalDateTime = LocalDateTime.now().withSecond(0).withNano(0)

            val tasksAndEventsByDays = activities
                .groupBy { it.startTimeLocal.toLocalDate() }

            val tasksWithBadDeadline = activities
                .asSequence()
                .filter { it.isTask && !it.isDeadlineMet }
                .map { it.activityId }.toSet().toList()
                .mapNotNull { id -> tasks.find { it.task.id == id } }
                .toList()

            private fun List<TaskUiModel>.filterTags() : List<TaskUiModel> {
                return this.filter { task ->
                    (/*showTasksWithoutTags &&*/ task.task.tagsIds.isEmpty()) ||
                            tags.any { it.show && (it.id in task.task.tagsIds) }
                }
            }
        }
        //data class Error(val message: String?) : UiState()
    }

    fun openTaskScreen(id: Long) {
        viewModelScope.launch {
            _navigationEvents.emit(NavigationEvent.NavigateToTaskScreen(id.toString()))
        }
    }

    fun openEventScreen(id: Long) {
        viewModelScope.launch {
            _navigationEvents.emit(NavigationEvent.NavigateToEventScreen(id.toString()))
        }
    }

    fun popBackStack() {
        viewModelScope.launch {
            _navigationEvents.emit(NavigationEvent.NavigateBack)
        }
    }

    fun openMatrixScreen() {
        viewModelScope.launch {
            _navigationEvents.emit(NavigationEvent.NavigateToMatrixScreen)
        }
    }

    fun openCalendarScreen() {
        viewModelScope.launch {
            _navigationEvents.emit(NavigationEvent.NavigateToCalendarScreen)
        }
    }

    fun openSettingsScreen() {
        viewModelScope.launch {
            _navigationEvents.emit(NavigationEvent.NavigateToSettings.Root)
        }
    }

    fun openScheduleSettingsScreen() {
        viewModelScope.launch {
            _navigationEvents.emit(NavigationEvent.NavigateToSettings.ScheduleScreen)
        }
    }

    fun openTimeSlotScreen(id: Long) {
        viewModelScope.launch {
            _navigationEvents.emit(NavigationEvent.NavigateToSettings.TimeSlotScreen(id.toString()))
        }
    }

    sealed class NavigationEvent {
        data class NavigateToTaskScreen(val id: String) : NavigationEvent()
        data class NavigateToEventScreen(val id: String) : NavigationEvent()
        object NavigateToMatrixScreen : NavigationEvent()
        object NavigateToCalendarScreen : NavigationEvent()
        sealed class NavigateToSettings : NavigationEvent() {
            object Root : NavigateToSettings()
            //object MainScreen : NavigateToSettings()
            object ScheduleScreen : NavigateToSettings()
            data class TimeSlotScreen(val id: String) : NavigateToSettings()
        }
        object NavigateBack : NavigationEvent()
    }

    private val _showTasksWithoutTags = mutableStateOf(true)
    val showTasksWithoutTags
        get() = _showTasksWithoutTags.value

    private val _showCompletedTasks = mutableStateOf(false)
    val showCompletedTasks
        get() = _showCompletedTasks.value

    private var zoneId = ZoneId.systemDefault()

    fun findTaskById(id: Long): TaskUiModel? {
        Log.i("mytag", "find")
        return uiState.value.tasks.find {it.task.id == id}
    }

    fun addTask() {
        viewModelScope.launch {
            val id = taskRepository.insert(Task())
            openTaskScreen(id)
        }
    }

    fun addChildTask(parentTask: TaskUiModel) {
        viewModelScope.launch {
            val childId = taskRepository.insert(Task(parentTaskId = parentTask.task.id))
            openTaskScreen(childId)
            taskRepository.update(
                parentTask.task.copy(childTasksIds = parentTask.task.childTasksIds.plus(childId))
            )
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            taskRepository.update(task)
        }
    }

    fun deleteTask(taskUiModel: TaskUiModel) {
        viewModelScope.launch {
            taskRepository.delete(taskUiModel.task)
        }
    }

    fun toRightZone(deadline: Long?) : Long? {
        return deadline?.let {
            Instant.ofEpochMilli(it)
                .atOffset(ZoneOffset.UTC)
                .toLocalDateTime()
                .atZone(zoneId)
                .toInstant()
                .toEpochMilli()
        }
    }

    fun addTag(tag: Tag, task: TaskUiModel) {
        viewModelScope.launch {
            val tagId = tagRepository.insert(tag)
            taskRepository.update(task.task.copy(tagsIds = task.task.tagsIds.plus(tagId)))
        }
    }

    fun updateTag(tag: Tag) {
        viewModelScope.launch {
            tagRepository.update(tag)
        }
    }

    fun deleteTag(tag: Tag) {
        viewModelScope.launch {
            tagRepository.delete(tag)
        }
    }

    fun findTagById(id: Long): Tag? {
        return uiState.value.tags.find {it.id == id}
    }

    fun addEvent(event: EventUiModel) {
        val correctedEvent = if (event.startTime == 0L || event.endTime == 0L) {
            event.copy(
                startTime = LocalDateTime.now()
                    .withNano(0)
                    .withSecond(0)
                    .atZone(zoneId)
                    .toInstant()
                    .toEpochMilli(),
                endTime = LocalDateTime.now()
                    .withNano(0)
                    .withSecond(0)
                    .plusHours(1)
                    .atZone(zoneId)
                    .toInstant()
                    .toEpochMilli()
            )
        } else {
            event
        }
        viewModelScope.launch {
            val id = eventRepository.insert(correctedEvent.toEvent())
            openEventScreen(id)
            timeLinedActivityRepository.insertAll(getActivitiesByEvent(correctedEvent.copy(id = id)))
        }
    }

    fun updateEvent(event: EventUiModel) {
        Log.i("mytag", event.toString())
        viewModelScope.launch {
            val oldEvent = findEventById(event.id)
            oldEvent?.let {
                eventRepository.update(event.toEvent())
                if (it.startTime != event.startTime || it.endTime != event.endTime) {
                    timeLinedActivityRepository.deleteAllByActivityId(event.id, false)
                    timeLinedActivityRepository.insertAll(getActivitiesByEvent(event))
                }
            }
        }
    }

    fun deleteEvent(event: EventUiModel) {
        viewModelScope.launch {
            eventRepository.delete(event.toEvent())
        }
    }

    fun findEventById(id: Long): EventUiModel? {
        return uiState.value.events.find {it.id == id}
    }

    fun updateTimelinedActivity(activityUiModel: ActivityUiModel) {
        viewModelScope.launch {
            timeLinedActivityRepository.update(activityUiModel.toActivity())
        }
    }

    fun addTimeSlot(timeSlotUiModel: TimeSlotUiModel) {
        viewModelScope.launch {
            val id = timeSlotRepository.insert(timeSlotUiModel.toTimeSlot())
            openTimeSlotScreen(id)
        }
    }

    fun updateTimeSlot(timeSlotUiModel: TimeSlotUiModel) {
        viewModelScope.launch {
            timeSlotRepository.update(timeSlotUiModel.toTimeSlot())
        }
    }

    fun deleteTimeSlot(timeSlotUiModel: TimeSlotUiModel) {
        viewModelScope.launch {
            timeSlotRepository.delete(timeSlotUiModel.toTimeSlot())
        }
    }

    fun ChangeTasksWithoutTagsVisibility(show: Boolean) {
        _showTasksWithoutTags.value = show
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

    private fun addTimelinedTasks(
        timelinedTasks: MutableList<ActivityUiModel>,
        tmpActivityList: MutableList<MutableList<ActivityUiModel>>,
        tmpTasks: MutableList<Pair<TaskUiModel, Int>>,
        freeTime: List<FreeTime>,
        tasks: List<TaskUiModel>,
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
                    if (!usedTasks[i] && (time.tagId == 0L || time.tagId in tasks[i].task.tagsIds)) {
                        suitableTaskIndex = i
                        break
                    }
                }
                var suitableTmpTaskIndex: Int? = null
                for (i in 0 until tmpTasks.size) {
                    if (suitableTaskIndex == null || suitableTaskIndex > tmpTasks[i].second) {
                        if (time.tagId == 0L || time.tagId in tmpTasks[i].first.task.tagsIds) {
                            if (suitableTmpTaskIndex == null || tmpTasks[suitableTmpTaskIndex].second > tmpTasks[i].second) {
                                suitableTmpTaskIndex = i
                            }
                        }
                    }
                }

                if (suitableTmpTaskIndex != null)  {
                    val i = suitableTmpTaskIndex
                    val tmpTask = tmpTasks[i].first
                    val tmpEnd = tmpStart.plusMinutes(tmpTask.task.requiredTime.toLong())
                    if (tmpEnd.isAfter(time.endTime)) {
                        tmpActivityList[i].add(
                            ActivityUiModel(
                                isTask = true,
                                activityId = tmpTask.task.id,
                                startTimeLocal = tmpStart,
                                endTimeLocal = time.endTime,
                            )
                        )
                        val timeRemaining = tmpTask.task.requiredTime - ChronoUnit.MINUTES.between(tmpStart, time.endTime).toInt()
                        tmpTasks[i] = Pair(tmpTask.copy(task = tmpTask.task.copy(requiredTime = timeRemaining)), tmpTasks[i].second)
                        tmpStart = time.endTime
                    } else {
                        tmpActivityList[i].add(
                            ActivityUiModel(
                                isTask = true,
                                activityId = tmpTask.task.id,
                                startTimeLocal = tmpStart,
                                endTimeLocal = tmpEnd,
                            )
                        )
                        timelinedTasks.addAll(tmpActivityList[i].mapIndexed { index, activity ->
                            activity.copy(
                                numberOfParts = tmpActivityList[i].size,
                                partIndex = index + 1,
                                isDeadlineMet = tmpTask.deadlineLocal?.let {!it.isBefore(tmpEnd)} ?: true
                            )
                        })
                        tmpTasks.removeAt(i)
                        tmpActivityList.removeAt(i)
                        tmpStart = tmpEnd
                    }
                } else if (suitableTaskIndex != null) {
                    val i = suitableTaskIndex
                    usedTasks[i] = true
                    val tmpEnd = tmpStart.plusMinutes(tasks[i].task.requiredTime.toLong())
                    if (tmpEnd.isAfter(time.endTime)) {
                        tmpActivityList.add(mutableListOf(ActivityUiModel(
                            isTask = true,
                            activityId = tasks[i].task.id,
                            startTimeLocal = tmpStart,
                            endTimeLocal = time.endTime,
                        )))
                        val timeRemaining = tasks[i].task.requiredTime - ChronoUnit.MINUTES.between(tmpStart, time.endTime).toInt()
                        tmpTasks.add(Pair(tasks[i].copy(task = tasks[i].task.copy(requiredTime = timeRemaining)), i))
                        tmpStart = time.endTime
                    } else {
                        timelinedTasks.add(
                            ActivityUiModel(
                                isTask = true,
                                isDeadlineMet = tasks[i].deadlineLocal?.let {!it.isBefore(tmpEnd)} ?: true,
                                activityId = tasks[i].task.id,
                                startTimeLocal = tmpStart,
                                endTimeLocal = tmpEnd,
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

    fun calculateTimeline(
        tasklist: List<TaskUiModel>,
        saveTimeline: Boolean = true,
    ) : List<TaskUiModel> {
        val startTime = LocalDateTime.now().withNano(0).withSecond(0)
        val eventActivities = uiState.value.activities
            .filter { !it.isTask }
            .filter { it.endTimeLocal.isAfter(startTime) }
        val tasks = tasklist
            .filter { it.task.progress != it.task.requiredTime }
            .map { it.copy(task = it.task.copy(requiredTime = (it.task.requiredTime - it.task.progress))) }
        var taskIndex = 0
        val tmpActivityList: MutableList<MutableList<ActivityUiModel>> = mutableListOf()
        val tmpTasks: MutableList<Pair<TaskUiModel, Int>> = mutableListOf()
        val usedTasks = MutableList(tasks.size) { false }
        var currentDate = startTime.toLocalDate()
        var freeTime = listOf<FreeTime>()
        var eventIndex = 0
        val timelinedTasks = mutableListOf<ActivityUiModel>()
        var f = getFreeTime(startTime, uiState.value.timeSlots, eventActivities, eventIndex)
        eventIndex = f.second
        freeTime = f.first
        while (taskIndex != tasks.size || tmpTasks.isNotEmpty()) {
            taskIndex = addTimelinedTasks(timelinedTasks, tmpActivityList, tmpTasks, freeTime, tasks, usedTasks, taskIndex)
            currentDate = currentDate.plusDays(1)
            f = getFreeTime(currentDate.atStartOfDay(), uiState.value.timeSlots, eventActivities, eventIndex)
            eventIndex = f.second
            freeTime = f.first
        }
        Log.i("mytag", timelinedTasks.toString())
        if (saveTimeline) {
            viewModelScope.launch {
                timeLinedActivityRepository.deleteAllTasks()
                timeLinedActivityRepository.insertAll(timelinedTasks.map {it.toActivity()})
            }
        }
        return timelinedTasks
            .asSequence()
            .filter { it.isTask && !it.isDeadlineMet }
            .map { it.activityId }.toSet().toList()
            .mapNotNull { id -> tasklist.find {it.task.id == id} }
            .toList()
    }

    fun calculateTimelineByImportance() : List<TaskUiModel> {
        val tasks = uiState.value.tasks.toMutableList()
        while (true) {
            val overdueTasks = calculateTimeline(tasks, false)
                .filter { it.task.importance > 5 }
                .sortedBy {  it.deadlineLocal ?: if (it.task.urgency < 6) LocalDateTime.MAX else LocalDateTime.MIN }
            var flag = false
            for (overdueTask in overdueTasks) {
                var lastUnimportantTaskIndex: Int? = null
                for (i in 0 until tasks.size) {
                    if (tasks[i].task.id == overdueTask.task.id) {
                        lastUnimportantTaskIndex?.let {
                            for (j in it until i) {
                                tasks[j] = tasks[j + 1].also { tasks[j + 1] = tasks[j] }
                            }
                            flag = true
                        }
                    } else if (tasks[i].task.importance < 6) {
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
        return calculateTimeline(tasks, true)
    }

    private fun getActivitiesByEvent(event: EventUiModel) : List<TimeLinedActivity>{
        val list = mutableListOf<TimeLinedActivity>()
        var eventStart = Instant.ofEpochMilli(event.startTime).atZone(zoneId).toLocalDateTime()
        val startDate = eventStart.toLocalDate()
        val eventEnd = Instant.ofEpochMilli(event.endTime).atZone(zoneId).toLocalDateTime()
        val endDate = if (eventEnd.hour == 0 && eventEnd.minute == 0) {
            eventEnd.toLocalDate().minusDays(1)
        } else {
            eventEnd.toLocalDate()
        }
        if (ChronoUnit.MINUTES.between(eventStart, eventEnd) > 0) {
            val numberOfParts = ChronoUnit.DAYS.between(startDate, endDate).toInt() + 1
            var partIndex = 1
            while (eventStart.toLocalDate() != endDate) {
                list.add(
                    TimeLinedActivity(
                    startTime = eventStart.atZone(zoneId).toInstant().toEpochMilli(),
                    endTime = eventStart
                        .toLocalDate()
                        .plusDays(1)
                        .atStartOfDay()
                        .atZone(zoneId)
                        .toInstant()
                        .toEpochMilli(),
                    numberOfParts = numberOfParts,
                    partIndex = partIndex,
                    isTask = false,
                    activityId = event.id
                )
                )
                eventStart = eventStart
                    .toLocalDate()
                    .plusDays(1)
                    .atStartOfDay()
                partIndex++
            }
            list.add(
                TimeLinedActivity(
                startTime = eventStart.atZone(zoneId).toInstant().toEpochMilli(),
                endTime = eventEnd.atZone(zoneId).toInstant().toEpochMilli(),
                numberOfParts = numberOfParts,
                partIndex = partIndex,
                isTask = false,
                activityId = event.id
            )
            )
        } else {
            Log.i("mytag", "event wrap error")
        }
        return list
    }

    private fun Task.toUiModel(): TaskUiModel {
        val deadlineLocal = this.deadline?.let {
            Instant.ofEpochMilli(it)
                .atZone(zoneId)
                .toLocalDateTime()
        }
        val deadlineText = deadlineLocal?.format(DateTimeFormatter.ofPattern("MMM d yyyy H:mm"))
        return TaskUiModel(this, deadlineLocal, deadlineText)
    }

    private fun Event.toUiModel(): EventUiModel {
        return EventUiModel(
            id = this.id,
            name = this.name,
            description = this.description,
            startTime = this.startTime,
            endTime = this.endTime,
            startTimeLocal = Instant.ofEpochMilli(this.startTime)
                .atZone(zoneId)
                .toLocalDateTime(),
            endTimeLocal = Instant.ofEpochMilli(this.endTime)
                .atZone(zoneId)
                .toLocalDateTime()
        )
    }
    private fun EventUiModel.toEvent(): Event {
        return Event(
            id = this.id,
            name = this.name,
            description = this.description,
            startTime = this.startTime,
            endTime = this.endTime,
        )
    }
    private fun TimeLinedActivity.toUiModel(): ActivityUiModel {
        return ActivityUiModel(
            id = this.id,
            startTime = this.startTime,
            endTime = this.endTime,
            numberOfParts = this.numberOfParts,
            partIndex = this.partIndex,
            isTask = this.isTask,
            isDone = this.isDone,
            isDeadlineMet = this.isDeadlineMet,
            activityId = this.activityId,
            startTimeLocal = Instant.ofEpochMilli(this.startTime)
                .atZone(zoneId)
                .toLocalDateTime(),
            endTimeLocal = Instant.ofEpochMilli(this.endTime)
                .atZone(zoneId)
                .toLocalDateTime()
        )
    }
    private fun ActivityUiModel.toActivity(): TimeLinedActivity {
        return TimeLinedActivity(
            id = this.id,
            startTime = this.startTimeLocal.atZone(zoneId).toInstant().toEpochMilli(),
            endTime = this.endTimeLocal.atZone(zoneId).toInstant().toEpochMilli(),
            numberOfParts = this.numberOfParts,
            partIndex = this.partIndex,
            isTask = this.isTask,
            isDone = this.isDone,
            isDeadlineMet = this.isDeadlineMet,
            activityId = this.activityId,
        )
    }
    private fun TimeSlot.toUiModel(): TimeSlotUiModel {
        return TimeSlotUiModel(
            id = this.id,
            tagId = this.tagId,
            daysOfWeek = this.daysOfWeek,
            startTime = this.startTime,
            endTime = this.endTime,
        )
    }
    private fun TimeSlotUiModel.toTimeSlot(): TimeSlot {
        return TimeSlot(
            id = this.id,
            tagId = this.tagId,
            daysOfWeek = this.daysOfWeek,
            startTime = this.startTime,
            endTime = this.endTime,
        )
    }

    private data class FreeTime(
        val startTime: LocalDateTime,
        val endTime: LocalDateTime,
        val tagId: Long
    )
}
