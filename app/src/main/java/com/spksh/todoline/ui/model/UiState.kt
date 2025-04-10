package com.spksh.todoline.ui.model

import com.spksh.todoline.data.Tag.Tag
import com.spksh.todoline.ui.SettingsUi
import java.time.LocalDate
import java.time.LocalDateTime

sealed class UiState {
    data class State(
        val tasks: List<TaskUiModel>,
        val tags: List<Tag>,
        val events: List<EventUiModel>,
        val activities: List<ActivityUiModel>,
        val timeSlots: List<TimeSlotUiModel>,
        val settings: SettingsUi
    ) : UiState() {
        val tasks_1 = tasks
            .filter {it.importance >= 6 && it.urgency >= 6}.tasksFilter()
        val tasks_2 = tasks
            .filter {it.importance >= 6 && it.urgency <= 5}.tasksFilter()
        val tasks_3 = tasks//.filter {it.progress != 1f }
            .filter {it.importance <= 5 && it.urgency >= 6}.tasksFilter()
        val tasks_4 = tasks//.filter {it.progress != 1f }
            .filter {it.importance <= 5 && it.urgency <= 5}.tasksFilter()

        val currentTime: LocalDateTime = LocalDateTime.now().withSecond(0).withNano(0)

        val tasksAndEventsByDays = activities
            .groupBy { it.startTimeLocal.toLocalDate() }

        private fun List<TaskUiModel>.tasksFilter() : List<TaskUiModel> {
            return this.filter { task ->
                ((settings.showTasksWithoutTags && task.tagsIds.isEmpty())
                        || tags.any { it.show && (it.id in task.tagsIds) })
                        && (settings.showCompletedTasks || task.progress != task.requiredTime)
                        && showOnlyTodayTasks(task)
                        && showOnlyWeekTasks(task)
                        && showOnlyMonthTasks(task)
            }
        }

        private fun showOnlyTodayTasks(task: TaskUiModel) : Boolean {
            return if (settings.showTodayTasks) {
                if (task.deadlineLocal == null) {
                    task.urgency >= 6
                } else {
                    task.deadlineLocal.toLocalDate().isAfter(LocalDate.now()).not()
                }
            } else {
                true
            }
        }

        private fun showOnlyWeekTasks(task: TaskUiModel) : Boolean {
            return if (settings.showWeekTasks) {
                if (task.deadlineLocal == null) {
                    task.urgency >= 6
                } else {
                    task.deadlineLocal.toLocalDate().isAfter(LocalDate.now()).not()
                            || (LocalDate.now().dayOfWeek < task.deadlineLocal.dayOfWeek
                            && LocalDate.now().plusWeeks(1L).isAfter(task.deadlineLocal.toLocalDate()))
                }
            } else {
                true
            }
        }

        private fun showOnlyMonthTasks(task: TaskUiModel) : Boolean {
            return if (settings.showTodayTasks) {
                if (task.deadlineLocal == null) {
                    task.urgency >= 6
                } else {
                    task.deadlineLocal.toLocalDate().isAfter(LocalDate.now()).not()
                            || (task.deadlineLocal.monthValue == LocalDate.now().monthValue
                            && LocalDate.now().plusMonths(1).isAfter(task.deadlineLocal.toLocalDate()))
                }
            } else {
                true
            }
        }
    }
}