package com.spksh.todoline.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task) : Long

    @Update
    suspend fun updateTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)

    @Query("SELECT * FROM task_table ORDER BY id ASC")
    suspend fun getAllTasks(): List<Task>

    @Query("SELECT * FROM task_table ORDER BY id ASC")
    fun getAllTasksFlow(): Flow<List<Task>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTag(tag: Tag) : Long

    @Update
    suspend fun updateTag(tag: Tag)

    @Delete
    suspend fun deleteTag(tag: Tag)

    @Query("SELECT * FROM tag_table ORDER BY id ASC")
    fun getAllTags(): List<Tag>

    @Query("SELECT * FROM tag_table ORDER BY id ASC")
    fun getAllTagsFlow(): Flow<List<Tag>>
}