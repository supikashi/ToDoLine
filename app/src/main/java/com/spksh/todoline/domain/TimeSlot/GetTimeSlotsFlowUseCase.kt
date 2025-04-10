package com.spksh.todoline.domain.TimeSlot

import com.spksh.todoline.data.TimeSlot.TimeSlot
import com.spksh.todoline.data.TimeSlot.TimeSlotRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTimeSlotsFlowUseCase @Inject constructor(
    private val timeSlotRepository: TimeSlotRepository
) {
    operator fun invoke(): Flow<List<TimeSlot>> {
        return timeSlotRepository.allItemsFlow
    }
}