package com.spksh.todoline.data.Base

import kotlinx.coroutines.flow.Flow

interface BaseDao<T> {
    suspend fun insert(item: T): Long
    suspend fun update(item: T)
    suspend fun delete(item: T)
    fun getAllItemsFlow(): Flow<List<T>>
}