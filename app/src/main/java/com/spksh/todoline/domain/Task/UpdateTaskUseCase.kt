package com.spksh.todoline.domain.Task

import com.spksh.todoline.data.Task.Task
import com.spksh.todoline.data.Task.TaskRepository
import javax.inject.Inject

class UpdateTaskUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    suspend operator fun invoke(task: Task, oldTask: Task? = null, time: Long = 0) {
        var newProgressDates = task.progressDates
        oldTask?.let {
            if (task.progress != it.progress) {
                val newProgress = task.progress - it.progressDates.sumOf { it.second }
                if (newProgress > 0) {
                    newProgressDates = newProgressDates.plus(
                        Pair(time, newProgress)
                    )
                }
            }
        }
        taskRepository.update(task.copy(progressDates = newProgressDates))
    }
}