package com.example.babful.ui.feed

import android.util.Log
import app.cash.turbine.test // ⭐️ [신규] Turbine의 .test { } 임포트
import com.example.babful.TestDispatchersRule // ⭐️ [신규] 방금 만든 Coroutine Rule
import com.example.babful.data.model.FeedItem
import com.example.babful.data.repository.FeedRepository
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import io.mockk.mockkStatic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi // ⭐️ TestDispatchersRule 사용
class FeedViewModelTest {

    // ⭐️ [신규] 1. Coroutine(Main Dispatcher) Rule 적용
    @get:Rule
    val testDispatchersRule = TestDispatchersRule()

    // ⭐️ 2. MockK Rule 적용
    @get:Rule
    val mockkRule = MockKRule(this)

    // ⭐️ 3. '가짜' Repository (26단계와 동일)
    @MockK
    lateinit var mockRepository: FeedRepository

    // ⭐️ 4. '테스트 대상'
    lateinit var viewModel: FeedViewModel

    // ⭐️ 5. (가짜) 데이터 정의
    private val mockCacheData = listOf(
        FeedItem(id = "cache_1", userName = "Room-Cache-User", radius = 5, content = "")
    )
    private val mockNetworkData = listOf(
        FeedItem(id = "api_1", userName = "Go-API-User", radius = 5, content = "")
    )
    private val apiException = RuntimeException("Go 서버 다운 (Mocked)")

    @Before
    fun setUp() {
        // ⭐️ (26단계와 동일) android.util.Log Mocking
        mockkStatic(Log::class)
        coEvery { Log.d(any(), any()) } returns 0
        coEvery { Log.e(any(), any()) } returns 0
        coEvery { Log.e(any(), any(), any()) } returns 0
    }

    // --- 테스트 케이스 ---

    @Test
    fun `refreshFeed - SWR 성공 시, '캐시' 먼저 방출 후 '네트워크' 데이터 방출`() = runTest {
        // Given (준비):
        // 1. '가짜' 배우(Repository)에게 '대본' 주기
        coEvery { mockRepository.getFeedItemsFromCache(5) } returns mockCacheData
        coEvery { mockRepository.getFeedItemsFromNetwork(5, true) } returns mockNetworkData

        // When (실행): ViewModel을 '생성'하면, init { } 블록에서 refreshFeed()가 '자동 호출'됨
        viewModel = FeedViewModel(mockRepository)

        // Then (검증): Turbine으로 uiState의 '흐름'을 검증
        viewModel.uiState.test {
            // ⭐️ 1. '기본 상태' 검증 (isLoading=true, 아이템 0개)
            // (init에서 refreshFeed가 불리면 isLoading=true로 시작)
            val initialState = awaitItem()
            assertThat(initialState.isLoading).isTrue()
            assertThat(initialState.feedItems).isEmpty()

            // ⭐️ 2. '캐시 로드' 상태 검증 (isLoading=false, 캐시 데이터 1개)
            val cacheState = awaitItem()
            assertThat(cacheState.isLoading).isFalse()
            assertThat(cacheState.feedItems.first().userName).isEqualTo("Room-Cache-User")

            // ⭐️ 3. '네트워크 로딩' 상태 검증 (isLoading=true, 캐시 데이터 1개)
            val networkLoadingState = awaitItem()
            assertThat(networkLoadingState.isLoading).isTrue()
            assertThat(networkLoadingState.feedItems.first().userName).isEqualTo("Room-Cache-User")

            // ⭐️ 4. '네트워크 완료' 상태 검증 (isLoading=false, 네트워크 데이터 1개)
            val networkDoneState = awaitItem()
            assertThat(networkDoneState.isLoading).isFalse()
            assertThat(networkDoneState.feedItems.first().userName).isEqualTo("Go-API-User")
        }
    }

    @Test
    fun `refreshFeed - 네트워크 실패 시, '캐시'만 방출하고 갱신 안 함`() = runTest {
        // Given (준비):
        // 1. '캐시'는 성공
        coEvery { mockRepository.getFeedItemsFromCache(5) } returns mockCacheData
        // 2. '네트워크'는 실패(예외)하도록 '연기'
        coEvery { mockRepository.getFeedItemsFromNetwork(5, true) } throws apiException

        // When (실행):
        viewModel = FeedViewModel(mockRepository)

        // Then (검증):
        viewModel.uiState.test {
            // 1. '기본 상태' (isLoading=true)
            assertThat(awaitItem().isLoading).isTrue()

            // 2. '캐시 로드' 상태 (isLoading=false, 캐시 데이터)
            val cacheState = awaitItem()
            assertThat(cacheState.isLoading).isFalse()
            assertThat(cacheState.feedItems.first().userName).isEqualTo("Room-Cache-User")

            // 3. '네트워크 로딩' 상태 (isLoading=true, 캐시 데이터)
            val networkLoadingState = awaitItem()
            assertThat(networkLoadingState.isLoading).isTrue()

            // 4. '네트워크 실패' 상태 (isLoading=false, 캐시 데이터 '유지')
            val networkFailedState = awaitItem()
            assertThat(networkFailedState.isLoading).isFalse()
            assertThat(networkFailedState.feedItems.first().userName).isEqualTo("Room-Cache-User")

            // ⭐️ 5. (중요) '네트워크'가 실패했으므로 Repository는 1번만 불려야 함 (캐시는 불리지 않음)
            coVerify(exactly = 1) { mockRepository.getFeedItemsFromNetwork(5, true) }
        }
    }
}