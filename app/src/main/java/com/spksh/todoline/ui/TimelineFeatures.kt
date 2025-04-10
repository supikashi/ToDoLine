package com.spksh.todoline.ui

import com.spksh.todoline.domain.TimeLine.GetTimelineByImportanceUseCase
import com.spksh.todoline.domain.TimeLine.GetTimelineUseCase
import com.spksh.todoline.ui.model.TaskUiModel
import com.spksh.todoline.ui.model.UiState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.StateFlow
import java.time.ZoneId

class TimelineFeatures @AssistedInject constructor(
    private val getTimelineUseCase: GetTimelineUseCase,
    private val getTimelineByImportanceUseCase: GetTimelineByImportanceUseCase,
    @Assisted private val scope: CoroutineScope,
    @Assisted private val zoneId: ZoneId,
    @Assisted private val flow: StateFlow<UiState.State>,
) {
    fun getTimeline(
        tasklist: List<TaskUiModel>,
        saveTimeline: Boolean = true,
    ): Deferred<List<TaskUiModel>?> {
        return scope.async {
            getTimelineUseCase(
                tasklist,
                saveTimeline,
                flow.value.activities,
                flow.value.timeSlots,
                zoneId
            )
        }
    }

    fun getTimelineByImportance(
        tasks: List<TaskUiModel>
    ) : Deferred<List<TaskUiModel>?> {
        return scope.async {
            getTimelineByImportanceUseCase(tasks, flow.value.activities, flow.value.timeSlots, zoneId)
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(
            scope: CoroutineScope,
            zoneId: ZoneId,
            flow: StateFlow<UiState.State>,
        ): TimelineFeatures
    }
}