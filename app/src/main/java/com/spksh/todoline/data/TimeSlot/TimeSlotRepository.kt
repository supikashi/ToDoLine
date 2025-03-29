package com.spksh.todoline.data.TimeSlot

import com.spksh.todoline.data.Base.BaseRepository
import com.spksh.todoline.data.Base.BaseRepositoryDelegate
import javax.inject.Inject

class TimeSlotRepository @Inject constructor(
    private val timeSlotDao: TimeSlotDao
) : BaseRepository<TimeSlot> by BaseRepositoryDelegate(timeSlotDao) {

}
