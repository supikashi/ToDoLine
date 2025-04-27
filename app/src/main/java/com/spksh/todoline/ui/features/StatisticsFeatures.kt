package com.spksh.todoline.ui.features

import com.spksh.todoline.domain.Statistics.GetStatisticsUseCase
import com.spksh.todoline.ui.model.Statistics
import com.spksh.todoline.ui.model.UiState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDate

class StatisticsFeatures @AssistedInject constructor(
    private val getStatisticsUseCase: GetStatisticsUseCase,
    @Assisted private val scope: CoroutineScope,
    @Assisted private val flow: StateFlow<UiState.State>,
) {
    fun getStatistics(startDate: LocalDate, endDate: LocalDate): Deferred<Statistics> {
        return scope.async {
            getStatisticsUseCase(startDate, endDate, flow.value.tags, flow.value.tasks)
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(
            scope: CoroutineScope,
            flow: StateFlow<UiState.State>,
        ): StatisticsFeatures
    }
}