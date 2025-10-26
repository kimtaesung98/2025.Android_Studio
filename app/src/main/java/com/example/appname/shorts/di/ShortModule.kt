package com.example.appname.shorts.di

import com.example.appname.shorts.data.repository.ShortsRepositoryImpl
import com.example.appname.shorts.domain.repository.ShortsRepository
import com.example.appname.shorts.domain.usecase.GetShortsUseCase
import com.example.appname.shorts.domain.usecase.LikeShortsUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import com.example.appname.shorts.domain.usecase.SubmitShortsCommentUseCase
import com.example.appname.shorts.domain.usecase.GetShortsCommentsUseCase
/**
 * [설계 의도 요약]
 * Hilt가 Shorts 모듈의 의존성을 주입(Inject)하는 방법을 정의합니다.
 */
@Module
@InstallIn(SingletonComponent::class)
object ShortsModule {

    /**
     * ShortsRepository(인터페이스)를 요청하면 ShortsRepositoryImpl(구현체)을 제공합니다.
     */
    @Provides
    @Singleton
    fun provideShortsRepository(): ShortsRepository {
        return ShortsRepositoryImpl()
    }

    /**
     * GetShortsUseCase 레시피를 정의합니다.
     */
    @Provides
    fun provideGetShortsUseCase(repository: ShortsRepository): GetShortsUseCase {
        return GetShortsUseCase(repository)
    }

    /**
     * LikeShortsUseCase 레시피를 정의합니다.
     */
    @Provides
    fun provideLikeShortsUseCase(repository: ShortsRepository): LikeShortsUseCase {
        return LikeShortsUseCase(repository)
    }
    // 🚨 (2) [New] GetShortsCommentsUseCase 레시피 추가
    @Provides
    fun provideGetShortsCommentsUseCase(repository: ShortsRepository): GetShortsCommentsUseCase {
        return GetShortsCommentsUseCase(repository)
    }

    // 🚨 (3) [New] SubmitShortsCommentUseCase 레시피 추가
    @Provides
    fun provideSubmitShortsCommentUseCase(repository: ShortsRepository): SubmitShortsCommentUseCase {
        return SubmitShortsCommentUseCase(repository)
    }
}