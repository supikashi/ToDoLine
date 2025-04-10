package com.spksh.todoline.domain.TimeSlot

import com.spksh.todoline.data.TimeSlot.TimeSlot
import com.spksh.todoline.data.TimeSlot.TimeSlotRepository
import javax.inject.Inject

class UpdateTimeSlotUseCase @Inject constructor(
    private val timeSlotRepository: TimeSlotRepository,
) {
    suspend operator fun invoke(timeSlot: TimeSlot) {
        timeSlotRepository.update(timeSlot)
    }
}