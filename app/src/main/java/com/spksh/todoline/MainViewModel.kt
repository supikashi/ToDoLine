package com.spksh.todoline

import android.util.Log
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spksh.todoline.data.Task
import com.spksh.todoline.data.TaskRepository
import kotlinx.coroutines.launch

class MainViewModel(private val repository: TaskRepository) : ViewModel() {

    //val tasks: StateFlow<List<Task>> = repository.allTasks
    //    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
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

    init {
        viewModelScope.launch {  ///пофиксить
            repository.allTasks.collect { dbValue ->
                Log.i("mytag", "bd update")
                _tasks.clear()
                _tasks.addAll(dbValue)
            }
        }
    }

    fun findTaskById(id: Int): Task? {
        return tasks.find {it.id == id}
    }

    fun addTask(task: Task) {
        viewModelScope.launch {
            repository.insert(task)
        }
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
        viewModelScope.launch {
            repository.delete(task)
        }
    }
}
