package com.spksh.todoline

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.spksh.todoline.data.DataStoreRepository
import com.spksh.todoline.data.TaskRepository

class MainViewModelFactory(
    private val taskRepository: TaskRepository,
    //private val dataStoreRepository: DataStoreRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(taskRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}