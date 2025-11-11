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
class FeedViewModel @Inject constructor( // ⭐️ [수정] 2. 생성자에 @Inject 및 Repository 추가
    private val repository: FeedRepository // Hilt가 이 파라미터를 '주입'해 줌
) : ViewModel() {

    private val _uiState = MutableStateFlow(FeedUiState())
    val uiState: StateFlow<FeedUiState> = _uiState.asStateFlow()

    private val radiusSteps = listOf(5, 10, 15, 20, 30)
    private var currentRadiusIndex = 0

    // ⭐️ [제거] 3. Repository '직접 생성' 코드 삭제
    // private val repository = FeedRepository()

    init {
        Log.d("FeedViewModel", "ViewModel이 생성(주입)되었습니다.")
        refreshFeed()
    }

    // [수정] 3. '새로고침' 함수 (5km로 리셋)
    fun refreshFeed() {
        Log.d("FeedViewModel", "새로고침 요청 받음 (5km 로드)")

        if (_uiState.value.isLoading || _uiState.value.isLoadingMore) return

        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {

            currentRadiusIndex = 0 // ⭐️ 인덱스 리셋
            val radius = radiusSteps[currentRadiusIndex]
            val freshItems = repository.getFeedItems(radius = radius, isRefresh = true)

            _uiState.update {
                it.copy(
                    isLoading = false,
                    feedItems = freshItems // ⭐️ 목록을 '교체'
                )
            }
            Log.d("FeedViewModel", "새로고침 완료 (현재 반경: ${radius}km)")
        }
    }

    // [수정] 4. '더 불러오기' 함수 (다음 반경으로 확장)
    fun loadMoreFeed() {
        if (_uiState.value.isLoading || _uiState.value.isLoadingMore) {
            Log.d("FeedViewModel", "이미 로딩 중... 요청 무시")
            return
        }

        // ⭐️ [신규] 5. 최대 반경 도달 여부 확인
        if (currentRadiusIndex >= radiusSteps.size - 1) {
            Log.d("FeedViewModel", "최대 반경(${radiusSteps.last()}km) 도달. 로드 중지.")
            return // 더 이상 로드할 반경이 없음
        }

        _uiState.update { it.copy(isLoadingMore = true) } // '더보기' 로딩 시작

        viewModelScope.launch {

            currentRadiusIndex++ // ⭐️ 다음 반경 인덱스로
            val nextRadius = radiusSteps[currentRadiusIndex]
            Log.d("FeedViewModel", "더 불러오기 요청 받음 -> Repository에 위임")

            val newItems = repository.getFeedItems(radius = nextRadius, isRefresh = false)

            _uiState.update { currentState ->
                currentState.copy(
                    isLoadingMore = false,
                    feedItems = currentState.feedItems + newItems // ⭐️ 목록에 '추가'
                )
            }
            Log.d("FeedViewModel", "더 불러오기 완료 (현재 반경: ${nextRadius}km)")
        }
    }
}