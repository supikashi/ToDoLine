package com.spksh.todoline.domain.Task

import com.spksh.todoline.data.Task.Task
import com.spksh.todoline.data.Task.TaskRepository
import javax.inject.Inject

class AddChildTaskUseCase @Inject constructor(
    private val taskRepository: TaskRepository,
    private val changeTasksOrderUseCase: ChangeTasksOrderUseCase
) {
    suspend operator fun invoke(parentTask: Task, order: List<Task>) : Long {
        val childId = taskRepository.insert(Task(parentTaskId = parentTask.id))
        changeTasksOrderUseCase(order.plus(Task(id = childId)))
        taskRepository.update(
            parentTask.copy(childTasksIds = parentTask.childTasksIds.plus(childId))
        )
        return childId
    }
}