package com.spksh.todoline.ui

import com.spksh.todoline.domain.Task.AddChildTaskUseCase
import com.spksh.todoline.domain.Task.AddTaskUseCase
import com.spksh.todoline.domain.Task.ChangeTasksOrderUseCase
import com.spksh.todoline.domain.Task.DeleteTaskUseCase
import com.spksh.todoline.domain.Task.UpdateTaskUseCase
import com.spksh.todoline.ui.MainViewModel.NavigationEvent
import com.spksh.todoline.ui.model.TaskUiModel
import com.spksh.todoline.ui.model.UiState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneId

class TaskFeatures @AssistedInject constructor(
    private val addTaskUseCase: AddTaskUseCase,
    private val addChildTaskUseCase: AddChildTaskUseCase,
    private val updateTaskUseCase: UpdateTaskUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase,
    private val changeTasksOrderUseCase: ChangeTasksOrderUseCase,
    @Assisted private val scope: CoroutineScope,
    @Assisted private val flow: StateFlow<UiState.State>,
    @Assisted private val navEvents: MutableSharedFlow<NavigationEvent>,
    @Assisted private val zoneId: ZoneId
) {
    fun add() {
        scope.launch {
            val id = addTaskUseCase(flow.value.settings.tasksOrder.map {it.toTask()})
            navEvents.emit(NavigationEvent.NavigateToTaskScreen(id.toString()))
        }
    }

    fun addChild(parentTask: TaskUiModel) {
        scope.launch {
            val id = addChildTaskUseCase(parentTask.toTask(), flow.value.settings.tasksOrder.map { it.toTask() })
            navEvents.emit(NavigationEvent.NavigateToTaskScreen(id.toString()))
        }
    }

    fun update(task: TaskUiModel) {
        scope.launch {
            val oldTask = flow.value.tasks.find { it.id == task.id}
            updateTaskUseCase(task.toTask(), oldTask?.toTask(), LocalDateTime.now().atZone(zoneId).toInstant().toEpochMilli())
        }
    }

    fun delete(task: TaskUiModel) {
        scope.launch {
            deleteTaskUseCase(task.toTask(), flow.value.settings.tasksOrder.map { it.toTask() })
        }
    }

    fun changeTaskOrder(order: List<TaskUiModel>) {
        scope.launch {
            changeTasksOrderUseCase(order.map {it.toTask()})
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(
            scope: CoroutineScope,
            flow: StateFlow<UiState.State>,
            navEvents: MutableSharedFlow<NavigationEvent>,
            zoneId: ZoneId
        ): TaskFeatures
    }
}