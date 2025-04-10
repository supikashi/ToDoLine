package com.spksh.todoline.domain.Task

import com.spksh.todoline.data.DataStoreRepository
import com.spksh.todoline.data.Task.Task
import javax.inject.Inject

class ChangeTasksOrderUseCase @Inject constructor(
    private val dataStoreRepository: DataStoreRepository
) {
    suspend operator fun invoke(order: List<Task>) {
        dataStoreRepository.saveTasksOrder(order.map {it.id})
    }
}