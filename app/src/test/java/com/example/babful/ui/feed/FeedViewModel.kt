package com.example.babful.ui.feed

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.babful.data.model.FeedItem
import com.example.babful.data.repository.FeedRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject
data class FeedUiState(
    val feedItems: List<FeedItem> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false
)
@HiltViewModel
class FeedViewModel @Inject constructor(
    private val repository: FeedRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FeedUiState())
    val uiState: StateFlow<FeedUiState> = _uiState.asStateFlow()

    private val radiusSteps = listOf(5, 10, 15, 20, 30)
    private var currentRadiusIndex = 0

    init {
        Log.d("FeedViewModel", "ViewModel이 생성(주입)되었습니다.")
        refreshFeed()
    }

    // [수정] 1. '새로고침' 함수 (SWR 적용)
    fun refreshFeed() {
        Log.d("FeedViewModel", "[SWR] 새로고침 요청 받음 (5km 로드)")
        if (_uiState.value.isLoading || _uiState.value.isLoadingMore) return
        _uiState.update { it.copy(isLoading = true) } // ⭐️ UI 스피너(새로고침) 시작

        viewModelScope.launch {
            // --- 1. 캐시 먼저 로드 ---
            val radius = radiusSteps[0]
            currentRadiusIndex = 0
            val cacheItems = repository.getFeedItemsFromCache(radius = radius)

            // ⭐️ UI 1차 업데이트 (캐시)
            _uiState.update {
                it.copy(
                    isLoading = false, // ⭐️ 캐시 로드는 빠르므로 스피너 바로 숨김
                    feedItems = cacheItems
                )
            }
            Log.d("FeedViewModel", "[SWR] 1. 캐시 표시 완료 (아이템: ${cacheItems.size}개)")

            // --- 2. 네트워크 갱신 (try-catch) ---
            try {
                // (네트워크 딜레이는 Go 서버가 아닌 여기서 제어)
                delay(1500) // UI 스피너를 보여주기 위한 가짜 딜레이
                _uiState.update { it.copy(isLoading = true) } // ⭐️ 네트워크 갱신 스피너 시작

                val networkItems = repository.getFeedItemsFromNetwork(radius = radius, isRefresh = true)

                // ⭐️ UI 2차 업데이트 (최신 데이터)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        feedItems = networkItems
                    )
                }
                Log.d("FeedViewModel", "[SWR] 2. 네트워크 갱신 완료 (아이템: ${networkItems.size}개)")
            } catch (e: Exception) {
                // (네트워크 실패 시, 캐시는 이미 보여줬으므로 로그만 남김)
                Log.e("FeedViewModel", "[SWR] 네트워크 갱신 실패", e)
                _uiState.update { it.copy(isLoading = false) } // 스피너 숨김
            }
        }
    }

    // [수정] 2. '더 불러오기' 함수 (SWR 적용)
    fun loadMoreFeed() {
        if (_uiState.value.isLoading || _uiState.value.isLoadingMore) return
        if (currentRadiusIndex >= radiusSteps.size - 1) return

        _uiState.update { it.copy(isLoadingMore = true) } // ⭐️ UI 스피너(더보기) 시작

        viewModelScope.launch {
            // --- 1. 캐시 먼저 로드 ---
            val nextIndex = currentRadiusIndex + 1
            val nextRadius = radiusSteps[nextIndex]
            val cacheItems = repository.getFeedItemsFromCache(radius = nextRadius)

            // ⭐️ UI 1차 업데이트 (기존 목록 + 캐시)
            _uiState.update { currentState ->
                currentState.copy(
                    isLoadingMore = false, // ⭐️ 캐시 로드는 빠르므로 스피너 바로 숨김
                    feedItems = currentState.feedItems + cacheItems
                )
            }
            Log.d("FeedViewModel", "[SWR] 1. (더보기) 캐시 표시 완료 (반경: ${nextRadius}km)")

            // --- 2. 네트워크 갱신 (try-catch) ---
            try {
                // (네트워크 딜레이는 Go 서버가 아닌 여기서 제어)
                delay(1500)
                _uiState.update { it.copy(isLoadingMore = true) } // ⭐️ 네트워크 갱신 스피너 시작

                val networkItems = repository.getFeedItemsFromNetwork(radius = nextRadius, isRefresh = false)
                currentRadiusIndex = nextIndex // ⭐️ 갱신 성공 시에만 인덱스 증가

                // ⭐️ UI 2차 업데이트 (기존 목록 - 캐시 + 최신 데이터)
                _uiState.update { currentState ->
                    currentState.copy(
                        isLoadingMore = false,
                        feedItems = (currentState.feedItems - cacheItems.toSet()) + networkItems
                    )
                }
                Log.d("FeedViewModel", "[SWR] 2. (더보기) 네트워크 갱신 완료 (반경: ${nextRadius}km)")
            } catch (e: Exception) {
                Log.e("FeedViewModel", "[SWR] (더보기) 네트워크 갱신 실패", e)
                _uiState.update { it.copy(isLoadingMore = false) } // 스피너 숨김
            }
        }
    }
}