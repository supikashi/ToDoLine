package com.spksh.todoline.data.TimeLinedActivity

import com.spksh.todoline.data.Base.BaseRepository
import com.spksh.todoline.data.Base.BaseRepositoryDelegate
import javax.inject.Inject

class TimeLinedActivityRepository @Inject constructor(
    private val timeLinedActivityDao: TimeLinedActivityDao
) : BaseRepository<TimeLinedActivity> by BaseRepositoryDelegate(timeLinedActivityDao) {

    suspend fun insertAll(timeLinedActivities: List<TimeLinedActivity>) : List<Long> {
        return timeLinedActivityDao.insertAll(timeLinedActivities)
    }

    suspend fun deleteAllByActivityId(activityId: Long, isTask: Boolean) {
        timeLinedActivityDao.deleteAllByActivityId(activityId, isTask)
    }

    suspend fun deleteAllTasks() {
        timeLinedActivityDao.deleteAllTasks()
    }
}