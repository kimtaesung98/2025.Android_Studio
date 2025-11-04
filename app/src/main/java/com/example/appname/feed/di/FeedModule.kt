package com.example.appname.feed.di

import com.example.appname.feed.data.repository.FeedRepositoryImpl
import com.example.appname.feed.domain.repository.FeedRepository
import com.example.appname.feed.domain.usecase.GetFeedPostsUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import com.example.appname.feed.domain.usecase.LikePostUseCase
import com.example.appname.feed.domain.usecase.SubmitCommentUseCase
import com.example.appname.feed.domain.usecase.GetCommentsUseCase
import com.example.appname.feed.data.local.dao.PostDao
import com.example.appname.feed.data.remote.api.FeedApi
import retrofit2.Retrofit
import com.example.appname.feed.domain.usecase.RefreshPostsUseCase
/**
 * [설계 의도 요약]
 * Hilt가 Feed 모듈의 의존성을 주입(Inject)하는 방법을 정의합니다.
 * ViewModel은 이 모듈 덕분에 UseCase를 자동으로 주입받을 수 있습니다.
 */
@Module
@InstallIn(SingletonComponent::class) // 이 모듈의 생명주기를 앱 전체로 설정
object FeedModule {

    /**
     * FeedRepository(인터페이스)를 요청하면 FeedRepositoryImpl(구현체)을 제공합니다.
     */
    @Provides
    @Singleton
    fun provideFeedRepository(
        feedApi: FeedApi,
        postDao: PostDao
    ): FeedRepository {
        return FeedRepositoryImpl(feedApi, postDao)
    }

    /**
     * GetFeedPostsUseCase를 요청하면, Hilt가 FeedRepository를 주입하여 생성해 줍니다.
     */
    @Provides
    fun provideGetFeedPostsUseCase(repository: FeedRepository): GetFeedPostsUseCase {
        return GetFeedPostsUseCase(repository)
    }

    // (2) 🚨 LikePostUseCase 레시피 추가
    @Provides
    fun provideLikePostUseCase(repository: FeedRepository): LikePostUseCase {
        return LikePostUseCase(repository)
    }
    // (3) 🚨 SubmitCommentUseCase 레시피 추가
    @Provides
    fun provideSubmitCommentUseCase(repository: FeedRepository): SubmitCommentUseCase {
        return SubmitCommentUseCase(repository)
    }
    // (2) [New] GetCommentsUseCase 레시피 추가
    @Provides
    fun provideGetCommentsUseCase(repository: FeedRepository): GetCommentsUseCase {
        return GetCommentsUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideFeedApi(retrofit: Retrofit): FeedApi {
        return retrofit.create(FeedApi::class.java)
    }
    @Provides
    fun provideRefreshPostsUseCase(repository: FeedRepository): RefreshPostsUseCase {
        return RefreshPostsUseCase(repository)
    }

}