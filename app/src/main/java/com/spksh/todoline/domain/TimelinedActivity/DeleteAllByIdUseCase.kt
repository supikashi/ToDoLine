package com.spksh.todoline.domain.TimelinedActivity

import com.spksh.todoline.data.TimeLinedActivity.TimeLinedActivityRepository
import javax.inject.Inject

class DeleteAllByIdUseCase @Inject constructor(
    private val activityRepository: TimeLinedActivityRepository,
) {
    suspend operator fun invoke(activityId: Long, isTask: Boolean) {
        activityRepository.deleteAllByActivityId(activityId, isTask)
    }
}