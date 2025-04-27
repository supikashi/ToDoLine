package com.spksh.todoline.domain.TimelinedActivity

import com.spksh.todoline.data.TimeLinedActivity.TimeLinedActivity
import com.spksh.todoline.data.TimeLinedActivity.TimeLinedActivityRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetActivitiesFlowUseCase @Inject constructor(
    private val activityRepository: TimeLinedActivityRepository
) {
    operator fun invoke(): Flow<List<TimeLinedActivity>> {
        return activityRepository.allItemsFlow
    }
}