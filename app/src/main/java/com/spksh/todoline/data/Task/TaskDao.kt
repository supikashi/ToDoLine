package com.spksh.todoline.data.Task

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.spksh.todoline.data.Base.BaseDao
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao : BaseDao<Task> {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    override suspend fun insert(item: Task) : Long

    @Update
    override suspend fun update(item: Task)

    @Delete
    override suspend fun delete(item: Task)

    @Query("SELECT * FROM task_table ORDER BY id ASC")
    override fun getAllItemsFlow(): Flow<List<Task>>

    @Query("SELECT * FROM task_table ORDER BY id ASC")
    suspend fun getAllItems(): List<Task>
}