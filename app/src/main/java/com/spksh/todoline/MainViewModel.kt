package com.spksh.todoline

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spksh.todoline.data.Tag
import com.spksh.todoline.data.Task
import com.spksh.todoline.data.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    //private val dataStoreRepository: DataStoreRepository
) : ViewModel() {

    private val _navigationEvents = MutableSharedFlow<NavigationEvent>()
    val navigationEvents = _navigationEvents.asSharedFlow()

    val uiState = combine(taskRepository.allTasks, taskRepository.allTags) { tasks, tags ->
        UiState.State(tasks.map {it.toUiModel()}, tags)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        UiState.State(emptyList(), emptyList())
    )

    sealed class UiState {
        //object Loading : UiState()
        data class State(
            val tasks: List<TaskUiModel>,
            val tags: List<Tag>
        ) : UiState() {
            val tasks_1
                get() = tasks//.filter {it.task.progress != 1f }
                    .filter {it.task.importance >= 6 && it.task.urgency >= 6}.filterTags().sortedBy { it.task.deadline ?: Long.MAX_VALUE }
            val tasks_2
                get() = tasks//.filter {it.task.progress != 1f }
                    .filter {it.task.importance >= 6 && it.task.urgency <= 5}.filterTags().sortedBy { it.task.deadline ?: Long.MAX_VALUE }
            val tasks_3
                get() = tasks//.filter {it.task.progress != 1f }
                    .filter {it.task.importance <= 5 && it.task.urgency >= 6}.filterTags().sortedBy { it.task.deadline ?: Long.MAX_VALUE }
            val tasks_4
                get() = tasks//.filter {it.task.progress != 1f }
                    .filter {it.task.importance <= 5 && it.task.urgency <= 5}.filterTags().sortedBy { it.task.deadline ?: Long.MAX_VALUE }

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

    sealed class NavigationEvent {
        data class NavigateToTaskScreen(val id: String) : NavigationEvent()
        object NavigateToMatrixScreen : NavigationEvent()
        object NavigateToCalendarScreen : NavigationEvent()
        object NavigateBack : NavigationEvent()
    }

    private val _showTasksWithoutTags = mutableStateOf(true)
    val showTasksWithoutTags
        get() = _showTasksWithoutTags.value

    private var zoneId = ZoneId.systemDefault()
    init {
        /*runBlocking {
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
        }*/
    }

    fun findTaskById(id: Long): TaskUiModel? {
        Log.i("mytag", "find")
        return uiState.value.tasks.find {it.task.id == id}
    }

    fun addTask() {
        viewModelScope.launch {
            val id = taskRepository.insertTask(Task())
            openTaskScreen(id)
        }
    }

    fun addChildTask(parentTask: TaskUiModel) {
        viewModelScope.launch {
            val childId = taskRepository.insertTask(Task(parentTaskId = parentTask.task.id))
            openTaskScreen(childId)
            taskRepository.updateTask(
                parentTask.task.copy(childTasksIds = parentTask.task.childTasksIds.plus(childId))
            )
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            taskRepository.updateTask(task)
        }
    }

    fun deleteTask(taskUiModel: TaskUiModel) {
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

    fun addTag(tag: Tag, task: TaskUiModel) {
        viewModelScope.launch {
            val tagId = taskRepository.insertTag(tag)
            taskRepository.updateTask(task.task.copy(tagsIds = task.task.tagsIds.plus(tagId)))
        }
    }

    fun updateTag(tag: Tag) {
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
        viewModelScope.launch {
            taskRepository.deleteTag(tag)
        }
    }

    fun findTagById(id: Long): Tag? {
        return uiState.value.tags.find {it.id == id}
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
        return TaskUiModel(this, text)
    }

}

// можно сделать обычным классом, сделать конструктор приватным, и добавить только паблик метод который принимает таск и таймзону
data class TaskUiModel(
    val task: Task = Task(),
    val deadlineText: String? = null,
    //val tagsList: List<Tag> = emptyList(),
)
