package com.spksh.todoline.data.Tag

import androidx.room.Transaction
import com.spksh.todoline.data.Base.BaseRepository
import com.spksh.todoline.data.Base.BaseRepositoryDelegate
import com.spksh.todoline.data.TimeSlot.TimeSlotDao
import com.spksh.todoline.data.Task.TaskDao
import javax.inject.Inject

class TagRepository @Inject constructor(
    private val tagDao: TagDao,
    private val taskDao: TaskDao,
    private val timeSlotDao: TimeSlotDao
) : BaseRepository<Tag> by BaseRepositoryDelegate(tagDao) {

    @Transaction
    override suspend fun delete(item: Tag) {
        val tasks = taskDao.getAllItems()
        tasks.filter { item.id in it.tagsIds } .forEach {
            taskDao.update(it.copy(tagsIds = it.tagsIds.minus(item.id)))
        }
        timeSlotDao.deleteAllWithTag(item.id)
        tagDao.delete(item)
    }
}