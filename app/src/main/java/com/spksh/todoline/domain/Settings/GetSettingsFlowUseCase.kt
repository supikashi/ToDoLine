package com.spksh.todoline.domain.Settings

import com.spksh.todoline.data.DataStoreRepository
import com.spksh.todoline.data.SettingsData
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSettingsFlowUseCase @Inject constructor(
    private val dataStoreRepository: DataStoreRepository
) {
    operator fun invoke(): Flow<SettingsData> {
        return dataStoreRepository.settingsDataFlow
    }
}