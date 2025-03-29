package com.spksh.todoline.data.Tag

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.spksh.todoline.data.Base.BaseDao
import kotlinx.coroutines.flow.Flow

@Dao
interface TagDao : BaseDao<Tag> {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    override suspend fun insert(item: Tag) : Long

    @Update
    override suspend fun update(item: Tag)

    @Delete
    override suspend fun delete(item: Tag)

    @Query("SELECT * FROM tag_table ORDER BY id ASC")
    override fun getAllItemsFlow(): Flow<List<Tag>>
}