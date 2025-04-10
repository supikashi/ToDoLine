package com.spksh.todoline.domain.Tag

import com.spksh.todoline.data.Tag.Tag
import com.spksh.todoline.data.Tag.TagRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTagsFlowUseCase @Inject constructor(
    private val tagRepository: TagRepository
) {
    operator fun invoke(): Flow<List<Tag>> {
        return tagRepository.allItemsFlow
    }
}