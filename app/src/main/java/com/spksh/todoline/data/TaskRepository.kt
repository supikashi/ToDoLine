package com.spksh.todoline.data

import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TaskRepository @Inject constructor(
    private val taskDao: TaskDao
) {

    val allTasks: Flow<List<Task>> = taskDao.getAllTasksFlow()

    suspend fun getCurrentTasks(): List<Task> {
        return taskDao.getAllTasks()
    }

    suspend fun insertTask(task: Task) : Long {
        return taskDao.insertTask(task)
    }

    suspend fun updateTask(task: Task) {
        taskDao.updateTask(task)
    }

    @Transaction
    suspend fun deleteTask(task: Task) {
        if (task.parentTaskId != null || task.childTasksIds.isNotEmpty()) {
            val tasks = getCurrentTasks()
            val parentTask = tasks.find { it.id == task.parentTaskId }
            parentTask?.let {
                updateTask(it.copy(childTasksIds = it.childTasksIds.minus(task.id)))
            }
            task.childTasksIds.forEach { id ->
                val childTask = tasks.find { it.id == id }
                childTask?.let {
                    updateTask(it.copy(parentTaskId = null))
                }
            }
        }
        taskDao.deleteTask(task)
    }

    val allTags: Flow<List<Tag>> = taskDao.getAllTagsFlow()

    suspend fun getCurrentTags(): List<Tag> {
        return taskDao.getAllTags()
    }

    suspend fun insertTag(tag: Tag) : Long {
        return taskDao.insertTag(tag)
    }

    suspend fun updateTag(tag: Tag) {
        taskDao.updateTag(tag)
    }

    @Transaction
    suspend fun deleteTag(tag: Tag) {
        val tasks = getCurrentTasks()
        tasks.filter { tag.id in it.tagsIds } .forEach {
            taskDao.updateTask(it.copy(tagsIds = it.tagsIds.minus(tag.id)))
        }
        taskDao.deleteTag(tag)
    }
}