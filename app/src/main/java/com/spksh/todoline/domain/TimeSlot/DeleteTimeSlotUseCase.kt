package com.spksh.todoline.domain.TimeSlot

import com.spksh.todoline.data.TimeSlot.TimeSlot
import com.spksh.todoline.data.TimeSlot.TimeSlotRepository
import javax.inject.Inject

class DeleteTimeSlotUseCase @Inject constructor(
    private val timeSlotRepository: TimeSlotRepository,
) {
    suspend operator fun invoke(timeSlot: TimeSlot) {
        timeSlotRepository.delete(timeSlot)
    }
}