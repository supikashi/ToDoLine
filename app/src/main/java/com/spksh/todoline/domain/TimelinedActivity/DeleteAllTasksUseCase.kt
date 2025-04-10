package com.spksh.todoline.domain.TimelinedActivity

import com.spksh.todoline.data.TimeLinedActivity.TimeLinedActivityRepository
import javax.inject.Inject

class DeleteAllTasksUseCase @Inject constructor(
    private val activityRepository: TimeLinedActivityRepository,
) {
    suspend operator fun invoke() {
        activityRepository.deleteAllTasks()
    }
}