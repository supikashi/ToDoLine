package com.spksh.todoline.domain.Task

import com.spksh.todoline.data.Task.Task
import com.spksh.todoline.data.Task.TaskRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTasksFlowUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    operator fun invoke(): Flow<List<Task>> {
        return taskRepository.allItemsFlow
    }
}