package com.spksh.todoline.domain.Settings

import com.spksh.todoline.data.DataStoreRepository
import javax.inject.Inject

class SetLanguageUseCase @Inject constructor(
    private val dataStoreRepository: DataStoreRepository
) {
    suspend operator fun invoke(check: Boolean) {
        return dataStoreRepository.saveLanguage(check)
    }
}