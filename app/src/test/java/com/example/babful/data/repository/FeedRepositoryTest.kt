package com.example.babful.data.repository

import android.util.Log
import com.example.babful.data.db.FeedDao
import com.example.babful.data.model.FeedItem
import com.example.babful.data.network.ApiService
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import io.mockk.mockkStatic
class FeedRepositoryTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    @MockK
    lateinit var mockApiService: ApiService

    @MockK
    lateinit var mockFeedDao: FeedDao

    lateinit var repository: FeedRepository

    // (가짜 데이터 정의는 26단계와 동일)
    private val mockNetworkFeedItem5km = FeedItem(
        id = "api_5km_1",
        userName = "API-5km-User",
        content = "Go 서버에서 옴",
        radius = 0
    )
    private val mockCacheFeedItem5km = FeedItem(
        id = "cache_5km_1",
        userName = "Room-5km-User",
        content = "Room DB 캐시에서 옴",
        radius = 5
    )

    @Before
    fun setUp() {
        // ⭐️ [신규] 1. android.util.Log의 'static' 함수들을 Mocking
        mockkStatic(Log::class)
        coEvery { Log.d(any(), any()) } returns 0 // Log.d(tag, msg)
        coEvery { Log.e(any(), any()) } returns 0 // Log.e(tag, msg)
        coEvery { Log.e(any(), any(), any()) } returns 0 // Log.e(tag, msg, tr)

        // ⭐️ 2. '가짜' 배우(API, DAO)가 '아무것도 안 함'을 기본값으로 설정
        coEvery { mockFeedDao.getFeedsByRadius(any()) } returns emptyList()
        coEvery { mockFeedDao.insertAll(any()) } returns Unit
        coEvery { mockFeedDao.clearAllFeeds() } returns Unit

        // ⭐️ 3. '진짜' 주인공(Repository)에게 '가짜' 배우들 주입
        repository = FeedRepository(mockApiService, mockFeedDao)
    }

    // --- 테스트 케이스 (함수명 영어로 변경) ---

    /** ⭐️ [수정] 한글 -> 영어 */
    @Test
    fun `getFeedItemsFromCache_loadsFromRoomSuccessfully`() = runTest {
        // Given (준비): DAO가 '캐시'를 반환하도록 '연기'시킨다
        coEvery { mockFeedDao.getFeedsByRadius(5) } returns listOf(mockCacheFeedItem5km)

        // When (실행): 캐시 조회 함수를 실행한다
        val result = repository.getFeedItemsFromCache(radius = 5)

        // Then (검증):
        assertThat(result).hasSize(1)
        assertThat(result.first().userName).isEqualTo("Room-5km-User")
        coVerify(exactly = 0) { mockApiService.getFeedItems(any()) }
    }

    /** ⭐️ [수정] 한글 -> 영어 */
    @Test
    fun `getFeedItemsFromNetwork_onSuccess_updatesRoomCache`() = runTest {
        // Given (준비):
        coEvery { mockApiService.getFeedItems(5) } returns listOf(mockNetworkFeedItem5km)
        val slot = slot<List<FeedItem>>()
        coEvery { mockFeedDao.insertAll(capture(slot)) } returns Unit

        // When (실행): '네트워크' 조회 함수를 실행한다 (isRefresh = true)
        val result = repository.getFeedItemsFromNetwork(radius = 5, isRefresh = true)

        // Then (검증):
        assertThat(result.first().userName).isEqualTo("API-5km-User")
        coVerify(exactly = 1) { mockFeedDao.clearAllFeeds() }
        coVerify(exactly = 1) { mockFeedDao.insertAll(any()) }

        val capturedList = slot.captured
        assertThat(capturedList.first().radius).isEqualTo(5)
        assertThat(capturedList.first().userName).isEqualTo("API-5km-User")
    }

    /** ⭐️ [수정] 한글 -> 영어 */
    @Test
    fun `getFeedItemsFromNetwork_onApiFailure_throwsException`() = runTest {
        // Given (준비): API가 'Exception'을 던지도록 '연기'시킨다
        val apiException = RuntimeException("Go 서버 다운 (Mocked)")
        coEvery { mockApiService.getFeedItems(5) } throws apiException

        // When (실행) & Then (검증):
        try {
            repository.getFeedItemsFromNetwork(radius = 5, isRefresh = true)
            assert(false) { "API 예외가 던져지지 않았습니다." }
        } catch (e: Exception) {
            assertThat(e).isEqualTo(apiException)
        }

        coVerify(exactly = 0) { mockFeedDao.clearAllFeeds() }
        coVerify(exactly = 0) { mockFeedDao.insertAll(any()) }
    }
}