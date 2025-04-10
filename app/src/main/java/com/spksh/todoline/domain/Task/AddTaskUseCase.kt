package com.spksh.todoline.domain.Task

import com.spksh.todoline.data.Task.Task
import com.spksh.todoline.data.Task.TaskRepository
import javax.inject.Inject

class AddTaskUseCase @Inject constructor(
    private val taskRepository: TaskRepository,
    private val changeTasksOrderUseCase: ChangeTasksOrderUseCase
) {
    suspend operator fun invoke(order: List<Task>) : Long {
        val id = taskRepository.insert(Task())
        changeTasksOrderUseCase(order.plus(Task(id = id)))
        return id
    }
}