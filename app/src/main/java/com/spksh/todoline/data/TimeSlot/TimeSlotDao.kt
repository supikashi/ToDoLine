package com.spksh.todoline.data.TimeSlot

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.spksh.todoline.data.Base.BaseDao
import kotlinx.coroutines.flow.Flow

@Dao
interface TimeSlotDao : BaseDao<TimeSlot> {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    override suspend fun insert(item: TimeSlot) : Long

    @Update
    override suspend fun update(item: TimeSlot)

    @Delete
    override suspend fun delete(item: TimeSlot)

    @Query("SELECT * FROM timeslot_table ORDER BY id ASC")
    override fun getAllItemsFlow(): Flow<List<TimeSlot>>

    @Query("DELETE FROM timeslot_table WHERE tagId = :tagId")
    suspend fun deleteAllWithTag(tagId: Long)
}