package com.spksh.todoline.ui.features

import com.spksh.todoline.data.Tag.Tag
import com.spksh.todoline.domain.Tag.AddTagUseCase
import com.spksh.todoline.domain.Tag.DeleteTagUseCase
import com.spksh.todoline.domain.Tag.UpdateTagUseCase
import com.spksh.todoline.ui.model.TaskUiModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


class TagFeatures @AssistedInject constructor(
    private val addTagUseCase: AddTagUseCase,
    private val updateTagUseCase: UpdateTagUseCase,
    private val deleteTagUseCase: DeleteTagUseCase,
    @Assisted private val scope: CoroutineScope,
) {
    fun add(tag: Tag, task: TaskUiModel) {
        scope.launch {
            addTagUseCase(tag, task.toTask())
        }
    }

    fun update(tag: Tag) {
        scope.launch {
            updateTagUseCase(tag)
        }
    }

    fun delete(tag: Tag) {
        scope.launch {
            deleteTagUseCase(tag)
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(
            scope: CoroutineScope,
        ): TagFeatures
    }
}