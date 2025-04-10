package com.spksh.todoline.domain.TimelinedActivity

import com.spksh.todoline.data.TimeLinedActivity.TimeLinedActivity
import com.spksh.todoline.data.TimeLinedActivity.TimeLinedActivityRepository
import javax.inject.Inject

class UpdateTimelinedActivityUseCase  @Inject constructor(
    private val activityRepository: TimeLinedActivityRepository,
) {
    suspend operator fun invoke(activity: TimeLinedActivity) {
        activityRepository.update(activity)
    }
}