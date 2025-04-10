package com.spksh.todoline.domain.Task

import com.spksh.todoline.data.Task.Task
import com.spksh.todoline.data.Task.TaskRepository
import javax.inject.Inject

class DeleteTaskUseCase @Inject constructor(
    private val taskRepository: TaskRepository,
    private val changeTasksOrderUseCase: ChangeTasksOrderUseCase
) {
    suspend operator fun invoke(task: Task, order: List<Task>) {
        taskRepository.delete(task)
        changeTasksOrderUseCase(order.filter {it.id != task.id})
    }
}