package com.example.appname.shorts.domain.usecase

import com.example.appname.shorts.domain.model.ShortsComment
import com.example.appname.shorts.domain.repository.ShortsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetShortsCommentsUseCase @Inject constructor(
    private val repository: ShortsRepository
) {
    operator fun invoke(shortsId: Int): Flow<List<ShortsComment>> {
        return repository.getComments(shortsId)
    }
}