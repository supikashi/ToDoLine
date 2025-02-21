package com.spksh.todoline.data

import kotlinx.coroutines.flow.Flow

class TaskRepository(private val taskDao: TaskDao) {
    val allTasks: Flow<List<Task>> = taskDao.getAllTasks()

    suspend fun insertTask(task: Task) {
        taskDao.insertTask(task)
    }

    suspend fun updateTask(task: Task) {
        taskDao.updateTask(task)
    }

    suspend fun deleteTask(task: Task) {
        taskDao.deleteTask(task)
    }

    val allTags: Flow<List<Tag>> = taskDao.getAllTags()

    suspend fun insertTag(tag: Tag) {
        taskDao.insertTag(tag)
    }

    suspend fun updateTag(tag: Tag) {
        taskDao.updateTag(tag)
    }

    suspend fun deleteTag(tag: Tag) {
        taskDao.deleteTag(tag)
    }
}