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
import javax.inject.Inject

data class FeedUiState(
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val feedItems: List<FeedItem> = emptyList()
)

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val repository: FeedRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FeedUiState())
    val uiState: StateFlow<FeedUiState> = _uiState.asStateFlow()

    private val radiusSteps = listOf(5, 10, 15)
    private var currentRadiusIndex = 0

    init {
        Log.d("FeedViewModel", "ViewModel이 생성(주입)되었습니다.")
        // ⭐️ [수정] 앱 시작 시 SWR 로직을 '캐시 -> 네트워크'에서 '네트워크 Only'로 변경
        refreshFeed()
    }

    // ⭐️ [수정] 1. '새로고침' 로직 (SWR '깜박임' 버그 Fix)
    fun refreshFeed() {
        Log.d("FeedViewModel", "[새로고침] 네트워크 갱신 요청 (radius: 5km)")
        currentRadiusIndex = 0 // 새로고침 시 5km로 리셋
        _uiState.update { it.copy(isLoading = true) } // 1. 스피너 시작

        viewModelScope.launch {
            try {
                delay(1500) // (가짜 딜레이)

                // 2. ⭐️ '네트워크'만 호출
                val networkItems = repository.getFeedItemsFromNetwork(
                    radius = radiusSteps[currentRadiusIndex],
                    isRefresh = true
                )

                // 3. ⭐️ UI 1차 갱신 (네트워크 데이터)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        feedItems = networkItems
                    )
                }
                Log.d("FeedViewModel", "[새로고침] 네트워크 갱신 완료")
            } catch (e: Exception) {
                Log.e("FeedViewModel", "[새로고침] 네트워크 실패", e)
                _uiState.update { it.copy(isLoading = false) } // 로딩 중지
            }
        }
    }

    // ⭐️ [수정] '더 보기' 로직 (isRefresh = false)
    fun loadMoreFeed() {
        if (currentRadiusIndex >= radiusSteps.size - 1 || _uiState.value.isLoadingMore) return
        _uiState.update { it.copy(isLoadingMore = true) }
        currentRadiusIndex++

        viewModelScope.launch {
            try {
                delay(1000)
                val nextRadius = radiusSteps[currentRadiusIndex]
                val newItems = repository.getFeedItemsFromNetwork(
                    radius = nextRadius,
                    isRefresh = false // ⭐️ (isRefresh=false 이므로 Room DB에 누적됨)
                )

                _uiState.update {
                    // ⭐️ (네트워크 응답에는 'isLiked'가 포함되어 있으므로 바로 합침)
                    it.copy(
                        isLoadingMore = false,
                        feedItems = it.feedItems + newItems
                    )
                }
            } catch (e: Exception) {
                Log.e("FeedViewModel", "[더 보기] 네트워크 실패", e)
                _uiState.update { it.copy(isLoadingMore = false) }
            }
        }
    }

    // ⭐️ [수정] 2. '좋아요 토글' 로직
    fun likeFeedItem(feedId: String) {
        val currentItem = _uiState.value.feedItems.find { it.id == feedId } ?: return

        // ⭐️ 1. '좋아요' 상태에 따라 '좋아요' 또는 '좋아요 취소' 결정
        val isLiked = currentItem.isLiked

        Log.d("FeedViewModel", "[토글 클릭] Feed ID: $feedId. 현재 상태: $isLiked")

        // ⭐️ 2. (Optimistic Update) UI 즉시 갱신
        _uiState.update { currentState ->
            val updatedList = currentState.feedItems.map { item ->
                if (item.id == feedId) {
                    val updatedItem = item.copy(
                        likesCount = (item.likesCount ?: 0) + (if (isLiked) -1 else 1) // ⭐️ 토글
                    )
                    updatedItem.isLiked = !isLiked // ⭐️ 토글
                    updatedItem
                } else { item }
            }
            currentState.copy(feedItems = updatedList)
        }

        // ⭐️ 3. (Network Request) 백엔드에 '좋아요' 또는 '좋아요 취소' 요청
        viewModelScope.launch {
            try {
                if (isLiked) {
                    // ⭐️ [신규] '좋아요 취소' API 호출
                    repository.unlikeFeedItem(feedId)
                    Log.d("FeedViewModel", "[토글] '좋아요 취소' 백엔드 요청 성공 (ID: $feedId)")
                } else {
                    // ⭐️ [기존] '좋아요' API 호출
                    repository.likeFeedItem(feedId)
                    Log.d("FeedViewModel", "[토글] '좋아요' 백엔드 요청 성공 (ID: $feedId)")
                }

            } catch (e: Exception) {
                // ⭐️ 4. (Rollback) 실패 시 UI 원상 복구
                Log.e("FeedViewModel", "[토글] 백엔드 요청 실패, UI 롤백 (ID: $feedId)", e)
                _uiState.update { currentState ->
                    val rolledBackList = currentState.feedItems.map { item ->
                        if (item.id == feedId) {
                            // (원래 상태로 롤백)
                            val rolledBackItem = item.copy(
                                likesCount = (item.likesCount ?: 0) + (if (isLiked) 1 else -1) // ⭐️ 롤백
                            )
                            rolledBackItem.isLiked = isLiked // ⭐️ 롤백
                            rolledBackItem
                        } else { item }
                    }
                    currentState.copy(feedItems = rolledBackList)
                }
            }
        }
    }
}