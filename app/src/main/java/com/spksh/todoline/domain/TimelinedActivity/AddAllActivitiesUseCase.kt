package com.spksh.todoline.domain.TimelinedActivity

import com.spksh.todoline.data.TimeLinedActivity.TimeLinedActivity
import com.spksh.todoline.data.TimeLinedActivity.TimeLinedActivityRepository
import javax.inject.Inject

class AddAllActivitiesUseCase @Inject constructor(
    private val activityRepository: TimeLinedActivityRepository,
) {
    suspend operator fun invoke(activities: List<TimeLinedActivity>) {
        activityRepository.insertAll(activities)
    }
}