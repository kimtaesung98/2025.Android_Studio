package com.example.babful.ui.shorts

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope // ⭐️ [신규]
import com.example.babful.data.model.ShortsItem
import com.example.babful.data.repository.ShortsRepository // ⭐️ [신규]
import dagger.hilt.android.lifecycle.HiltViewModel // ⭐️ [신규]
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch // ⭐️ [신규]
import javax.inject.Inject // ⭐️ [신규]
import kotlinx.coroutines.delay
// ⭐️ [수정] isLoading 상태 추가 (UI 스피너 연동용)
data class ShortsUiState(
    val shortsItems: List<ShortsItem> = emptyList(),
    val isLoading: Boolean = false
)
@HiltViewModel
class ShortsViewModel @Inject constructor(
    private val repository: ShortsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ShortsUiState())
    val uiState: StateFlow<ShortsUiState> = _uiState.asStateFlow()

    init {
        Log.d("ShortsViewModel", "ViewModel이 생성(주입)되었습니다.")
        loadShorts()
    }

    // [수정] SWR 로직 적용 (DeliveryViewModel과 동일)
    private fun loadShorts() {
        Log.d("ShortsViewModel", "[SWR] 쇼츠 목록 로드 요청")
        _uiState.update { it.copy(isLoading = true) } // ⭐️ UI 스피너 시작

        viewModelScope.launch {
            // --- 1. 캐시 먼저 로드 ---
            val cacheItems = repository.getShortsItemsFromCache()

            // ⭐️ UI 1차 업데이트 (캐시)
            _uiState.update {
                it.copy(
                    isLoading = false, // ⭐️ 캐시 로드는 빠르므로 스피너 바로 숨김
                    shortsItems = cacheItems
                )
            }
            Log.d("ShortsViewModel", "[SWR] 1. 캐시 표시 완료 (아이템: ${cacheItems.size}개)")

            // --- 2. 네트워크 갱신 (try-catch) ---
            try {
                // (가짜 딜레이)
                delay(1000)
                _uiState.update { it.copy(isLoading = true) } // ⭐️ 네트워크 갱신 스피너 시작

                val networkItems = repository.getShortsItemsFromNetwork()

                // ⭐️ UI 2차 업데이트 (최신 데이터)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        shortsItems = networkItems
                    )
                }
                Log.d("ShortsViewModel", "[SWR] 2. 네트워크 갱신 완료 (아이템: ${networkItems.size}개)")
            } catch (e: Exception) {
                Log.e("ShortsViewModel", "[SWR] 네트워크 갱신 실패", e)
                _uiState.update { it.copy(isLoading = false) } // 스피너 숨김
            }
        }
    }
}