package com.spksh.todoline.ui.features

import com.spksh.todoline.domain.TimelinedActivity.UpdateTimelinedActivityUseCase
import com.spksh.todoline.ui.model.ActivityUiModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.ZoneId

class TimelinedActivityFeatures @AssistedInject constructor(
    private val updateTimelinedActivityUseCase: UpdateTimelinedActivityUseCase,
    @Assisted private val scope: CoroutineScope,
    @Assisted private val zoneId: ZoneId
) {
    fun update(activityUiModel: ActivityUiModel) {
        scope.launch {
            updateTimelinedActivityUseCase(activityUiModel.toActivity(zoneId))
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(
            scope: CoroutineScope,
            zoneId: ZoneId
        ): TimelinedActivityFeatures
    }
}