package com.example.appname.shorts.domain.usecase

import com.example.appname.shorts.domain.repository.ShortsRepository
import javax.inject.Inject

class RefreshShortsUseCase @Inject constructor(
    private val repository: ShortsRepository
) {
    suspend operator fun invoke(): Result<Boolean> {
        return repository.refreshShortsItems()
    }
}