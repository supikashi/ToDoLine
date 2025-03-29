package com.spksh.todoline.data.Event

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.spksh.todoline.data.Base.BaseDao
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao : BaseDao<Event> {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    override suspend fun insert(item: Event) : Long

    @Update
    override suspend fun update(item: Event)

    @Delete
    override suspend fun delete(item: Event)

    @Query("SELECT * FROM event_table ORDER BY id ASC")
    override fun getAllItemsFlow(): Flow<List<Event>>
}