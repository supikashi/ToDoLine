package com.spksh.todoline.data.TimeLinedActivity

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.spksh.todoline.data.Base.BaseDao
import kotlinx.coroutines.flow.Flow

@Dao
interface TimeLinedActivityDao : BaseDao<TimeLinedActivity> {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    override suspend fun insert(item: TimeLinedActivity) : Long

    @Update
    override suspend fun update(item: TimeLinedActivity)

    @Delete
    override suspend fun delete(item: TimeLinedActivity)

    @Query("SELECT * FROM activity_table ORDER BY id ASC")
    override fun getAllItemsFlow(): Flow<List<TimeLinedActivity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(itemsList: List<TimeLinedActivity>): List<Long>

    @Query("DELETE FROM activity_table WHERE activityId = :activityId AND isTask = :isTask")
    suspend fun deleteAllByActivityId(activityId: Long, isTask: Boolean)

    @Query("DELETE FROM activity_table WHERE isTask = 1")
    suspend fun deleteAllTasks()

    @Query("SELECT * FROM activity_table ORDER BY id ASC")
    suspend fun getAllItems(): List<TimeLinedActivity>
}