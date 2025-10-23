package com.example.appname.shorts.domain.usecase

import com.example.appname.shorts.domain.model.ShortsItem
import com.example.appname.shorts.domain.repository.ShortsRepository
import kotlinx.coroutines.flow.Flow

/**
 * [설계 의도 요약]
 * "쇼츠 목록을 가져온다"는 단일 비즈니스 로직(UseCase)을 캡슐화합니다.
 */
class GetShortsUseCase(
    private val repository: ShortsRepository
) {
    operator fun invoke(): Flow<List<ShortsItem>> {
        // TODO: implement details (예: 비즈니스 로직 추가)
        return repository.getShortsItems()
    }
}