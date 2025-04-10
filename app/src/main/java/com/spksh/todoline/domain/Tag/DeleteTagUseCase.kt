package com.spksh.todoline.domain.Tag

import com.spksh.todoline.data.Tag.Tag
import com.spksh.todoline.data.Tag.TagRepository
import javax.inject.Inject

class DeleteTagUseCase @Inject constructor(
    private val tagRepository: TagRepository
) {
    suspend operator fun invoke(tag: Tag) {
        tagRepository.delete(tag)
    }
}