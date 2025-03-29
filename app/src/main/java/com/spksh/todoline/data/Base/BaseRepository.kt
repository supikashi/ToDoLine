package com.spksh.todoline.data.Base

import kotlinx.coroutines.flow.Flow

interface BaseRepository<T> {
    val allItemsFlow: Flow<List<T>>
    suspend fun insert(item: T): Long
    suspend fun update(item: T)
    suspend fun delete(item: T)
}