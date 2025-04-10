package com.spksh.todoline.domain.Tag

import com.spksh.todoline.data.Tag.Tag
import com.spksh.todoline.data.Tag.TagRepository
import com.spksh.todoline.data.Task.Task
import com.spksh.todoline.domain.Task.UpdateTaskUseCase

import javax.inject.Inject

class AddTagUseCase @Inject constructor(
    private val tagRepository: TagRepository,
    private val updateTaskUseCase: UpdateTaskUseCase
) {
    suspend operator fun invoke(tag: Tag, task: Task) {
        val tagId = tagRepository.insert(tag)
        updateTaskUseCase(task.copy(tagsIds = task.tagsIds.plus(tagId)))
    }
}