package com.spksh.todoline.data.Task

import androidx.room.Transaction
import com.spksh.todoline.data.Base.BaseRepository
import com.spksh.todoline.data.Base.BaseRepositoryDelegate
import com.spksh.todoline.data.TimeLinedActivity.TimeLinedActivityDao
import javax.inject.Inject

class TaskRepository @Inject constructor(
    private val taskDao: TaskDao,
    private val activityDao: TimeLinedActivityDao
) : BaseRepository<Task> by BaseRepositoryDelegate(taskDao) {

    @Transaction
    override suspend fun delete(item: Task) {
        if (item.parentTaskId != null || item.childTasksIds.isNotEmpty()) {
            val tasks = taskDao.getAllItems()
            val parentTask = tasks.find { it.id == item.parentTaskId }
            parentTask?.let {
                taskDao.update(it.copy(childTasksIds = it.childTasksIds.minus(item.id)))
            }
            item.childTasksIds.forEach { id ->
                val childTask = tasks.find { it.id == id }
                childTask?.let {
                    taskDao.update(it.copy(parentTaskId = null))
                }
            }
        }
        activityDao.deleteAllByActivityId(item.id, true)
        taskDao.delete(item)
    }
}