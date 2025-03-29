package com.spksh.todoline.data.Event

import androidx.room.Transaction
import com.spksh.todoline.data.Base.BaseRepository
import com.spksh.todoline.data.Base.BaseRepositoryDelegate
import com.spksh.todoline.data.TimeLinedActivity.TimeLinedActivityDao
import javax.inject.Inject

class EventRepository @Inject constructor(
    private val eventDao: EventDao,
    private val timeLinedActivityDao: TimeLinedActivityDao
) : BaseRepository<Event> by BaseRepositoryDelegate(eventDao) {

    @Transaction
    override suspend fun delete(item: Event) {
        timeLinedActivityDao.deleteAllByActivityId(item.id, false)
        eventDao.delete(item)
    }
}