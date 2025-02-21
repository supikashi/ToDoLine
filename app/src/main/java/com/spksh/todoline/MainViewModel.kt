package com.spksh.todoline

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spksh.todoline.data.Tag
import com.spksh.todoline.data.Task
import com.spksh.todoline.data.TaskRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class MainViewModel(
    private val taskRepository: TaskRepository,
    //private val dataStoreRepository: DataStoreRepository
) : ViewModel() {

    private val _tasks = listOf<TaskUiModel>().toMutableStateList()
    val tasks
        get() = _tasks.toList().filter { task -> // не оптимально
            (showTasksWithoutTags && task.task.tagsIds.isEmpty()) ||
                    _tags.any { it.show && (it.id in task.task.tagsIds) }
        }

    private val _tags = listOf<Tag>().toMutableStateList()
    val tags
        get() = _tags.toList()

    val tasks_1
        get() = tasks.toList().filter {it.task.importance >= 6 && it.task.urgency >= 6}
    val tasks_2
        get() = tasks.toList().filter {it.task.importance >= 6 && it.task.urgency <= 5}
    val tasks_3
        get() = tasks.toList().filter {it.task.importance <= 5 && it.task.urgency >= 6}
    val tasks_4
        get() = tasks.toList().filter {it.task.importance <= 5 && it.task.urgency <= 5}

    private val _showTasksWithoutTags = mutableStateOf(true)
    val showTasksWithoutTags
        get() = _showTasksWithoutTags.value

    private var nextTaskId = -1
    private var nextTagId = -1
    private var zoneId = ZoneId.systemDefault()

    init {
        runBlocking {
            val dbTags = taskRepository.allTags.first()
            dbTags.forEach {
                Log.i("mytag", "$it")
            }
            _tags.clear()
            //_tags.add(Tag(id = 0, name = "Without Tag"))
            _tags.addAll(dbTags)
            if (_tags.isEmpty()) {
                nextTagId = 1
            } else {
                nextTagId = _tags.last().id + 1
            }

            val dbValue = taskRepository.allTasks.first()
            Log.i("mytag", LocalDateTime.now().toString())
            Log.i("mytag", "bd update")
            dbValue.forEach { Log.i("list", "$it") }
            if (dbValue.isEmpty()) {
                nextTaskId = 1
            } else {
                nextTaskId = dbValue.last().id + 1
            }
            _tasks.clear()
            dbValue.forEach { _tasks.add(it.toUiModel()) }
        }
    }

    fun findTaskById(id: Int): TaskUiModel? {
        return _tasks.find {it.task.id == id}
    }

    fun addTask(task: Task = Task()): Int {
        val taskUiModel = task.copy(id = nextTaskId++).toUiModel()
        _tasks.add(taskUiModel)
        viewModelScope.launch {
            taskRepository.insertTask(taskUiModel.task)
        }
        return taskUiModel.task.id
    }

    fun updateTask(task: Task) {
        val index = _tasks.withIndex().find {it.value.task.id == task.id}?.index
        index?.let {
            _tasks[it] = task.toUiModel()
        }
        viewModelScope.launch {
            taskRepository.updateTask(task)
        }
    }

    fun deleteTask(taskUiModel: TaskUiModel) {
        val parentTask = findTaskById(taskUiModel.task.parentTaskId ?: -1)
        parentTask?.let {
            updateTask(it.task.copy(childTasksIds = it.task.childTasksIds.minus(taskUiModel.task.id)))
        }
        taskUiModel.task.childTasksIds.forEach { id ->
            val childTask = findTaskById(id)
            childTask?.let {
                updateTask(it.task.copy(parentTaskId = null))
            }
        }
        _tasks.remove(taskUiModel)
        viewModelScope.launch {
            taskRepository.deleteTask(taskUiModel.task)
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

    fun addTag(tag: Tag): Int {
        val tagWithId = tag.copy(id = nextTagId++)
        _tags.add(tagWithId)
        viewModelScope.launch {
            taskRepository.insertTag(tagWithId)
        }
        return tagWithId.id
    }

    fun updateTag(tag: Tag) {
        val index = _tags.withIndex().find {it.value.id == tag.id}?.index
        if (index != null) {
            _tags[index] = tag
        }
        /*_tasks.forEach {
            if (tag.id in it.task.tagsIds) {
                updateTask(it.task)
            }
        }*/
        viewModelScope.launch {
            taskRepository.updateTag(tag)
        }
    }

    fun deleteTag(tag: Tag) {
        _tasks.forEach {
            if (tag.id in it.task.tagsIds) {
                updateTask(it.task.copy(tagsIds = it.task.tagsIds.minus(tag.id)))
            }
        }
        _tags.remove(tag)
        viewModelScope.launch {
            taskRepository.deleteTag(tag)
        }
    }

    fun findTagById(id: Int): Tag? {
        return _tags.find {it.id == id}
    }

    fun ChangeTasksWithoutTagsVisibility(show: Boolean) {
        _showTasksWithoutTags.value = show
    }

    private fun Task.toUiModel(): TaskUiModel {
        val text = this.deadline?.let {
            Instant.ofEpochMilli(it)
                .atZone(zoneId)
                .toLocalDateTime()
                .format(DateTimeFormatter.ofPattern("MMM d yyyy H:mm"))
        }
        //val tagsList: List<Tag> = this.tagsIds.mapNotNull { findTagById(it) }
        return TaskUiModel(this, text, /*tagsList*/)
    }

}

// можно сделать обычным классом, сделать конструктор приватным, и добавить только паблик метод который принимает таск и таймзону
data class TaskUiModel(
    val task: Task = Task(),
    val deadlineText: String? = null,
    //val tagsList: List<Tag> = emptyList(),
)
