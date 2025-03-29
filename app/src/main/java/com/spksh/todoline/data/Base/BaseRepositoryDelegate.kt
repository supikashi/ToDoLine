package com.spksh.todoline.data.Base

import kotlinx.coroutines.flow.Flow

class BaseRepositoryDelegate<T>(
    private val dao: BaseDao<T>
) : BaseRepository<T> {
    override val allItemsFlow: Flow<List<T>> = dao.getAllItemsFlow()
    override suspend fun insert(item: T) : Long = dao.insert(item)
    override suspend fun delete(item: T) = dao.delete(item)
    override suspend fun update(item: T) = dao.update(item)
}