package com.spksh.todoline

import android.util.Log
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spksh.todoline.data.Task
import com.spksh.todoline.data.TaskRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.time.Instant
import java.time.LocalDateTime

class MainViewModel(private val repository: TaskRepository) : ViewModel() {

    private val _tasks = listOf<Task>().toMutableStateList()
    val tasks
        get() = _tasks.toList()

    val tasks_1
        get() = _tasks.toList().filter {it.importance >= 6 && it.urgency >= 6}
    val tasks_2
        get() = _tasks.toList().filter {it.importance >= 6 && it.urgency <= 5}
    val tasks_3
        get() = _tasks.toList().filter {it.importance <= 5 && it.urgency >= 6}
    val tasks_4
        get() = _tasks.toList().filter {it.importance <= 5 && it.urgency <= 5}

    private var nextId = -1

    init {
        runBlocking {
            val dbValue = repository.allTasks.first()
            Log.i("mytag", LocalDateTime.now().toString())
            Log.i("mytag", "bd update")
            dbValue.forEach { Log.i("list", "$it") }
            if (dbValue.isEmpty()) {
                nextId = 1
            } else {
                nextId = dbValue.last().id + 1
            }
            _tasks.clear()
            _tasks.addAll(dbValue)
        }
    }

    fun findTaskById(id: Int): Task? {
        return _tasks.find {it.id == id}
    }

    fun addTask(): Int {
        val task = Task(id = nextId++)
        _tasks.add(task)
        viewModelScope.launch {
            repository.insert(task)
        }
        return task.id
    }

    fun updateTask(task: Task) {
        val index = _tasks.withIndex().find {it.value.id == task.id}?.index
        if (index != null) {
            _tasks[index] = task
        }
        viewModelScope.launch {
            repository.update(task)
        }
    }

    fun deleteTask(task: Task) {
        _tasks.remove(task)
        viewModelScope.launch {
            repository.delete(task)
        }
    }
}

/*data class TaskUiModel(
    val id: Int = 0,
    val name: String = "",
    val description: String = "",
    val importance: Int = 10,
    val urgency: Int = 10,
    val deadline: Instant? = null,
    val isDone: Boolean = false,
    val deadlineText: String = "",
)*/
