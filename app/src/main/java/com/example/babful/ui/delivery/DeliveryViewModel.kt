package com.example.babful.ui.delivery

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope // ⭐️ [신규]
import com.example.babful.data.model.DeliveryItem
import com.example.babful.data.repository.DeliveryRepository // ⭐️ [신규]
import dagger.hilt.android.lifecycle.HiltViewModel // ⭐️ [신규]
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch // ⭐️ [신규]
import javax.inject.Inject // ⭐️ [신규]
import kotlinx.coroutines.delay

// ⭐️ [수정] isLoading 상태 추가 (UI 스피너 연동용)
data class DeliveryUiState(
    val deliveryItems: List<DeliveryItem> = emptyList(),
    val isLoading: Boolean = false
)

@HiltViewModel
class DeliveryViewModel @Inject constructor(
    private val repository: DeliveryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DeliveryUiState())
    val uiState: StateFlow<DeliveryUiState> = _uiState.asStateFlow()

    init {
        Log.d("DeliveryViewModel", "ViewModel이 생성(주입)되었습니다.")
        loadDeliveryOrders()
    }

    // [수정] SWR 로직 적용 (FeedViewModel의 refreshFeed와 유사)
    private fun loadDeliveryOrders() {
        Log.d("DeliveryViewModel", "[SWR] 배달 목록 로드 요청")
        _uiState.update { it.copy(isLoading = true) } // ⭐️ UI 스피너 시작

        viewModelScope.launch {
            // --- 1. 캐시 먼저 로드 ---
            val cacheItems = repository.getDeliveryItemsFromCache()

            // ⭐️ UI 1차 업데이트 (캐시)
            _uiState.update {
                it.copy(
                    isLoading = false, // ⭐️ 캐시 로드는 빠르므로 스피너 바로 숨김
                    deliveryItems = cacheItems
                )
            }
            Log.d("DeliveryViewModel", "[SWR] 1. 캐시 표시 완료 (아이템: ${cacheItems.size}개)")

            // --- 2. 네트워크 갱신 (try-catch) ---
            try {
                // (가짜 딜레이)
                delay(1000)
                _uiState.update { it.copy(isLoading = true) } // ⭐️ 네트워크 갱신 스피너 시작

                val networkItems = repository.getDeliveryItemsFromNetwork()

                // ⭐️ UI 2차 업데이트 (최신 데이터)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        deliveryItems = networkItems
                    )
                }
                Log.d("DeliveryViewModel", "[SWR] 2. 네트워크 갱신 완료 (아이템: ${networkItems.size}개)")
            } catch (e: Exception) {
                Log.e("DeliveryViewModel", "[SWR] 네트워크 갱신 실패", e)
                _uiState.update { it.copy(isLoading = false) } // 스피너 숨김
            }
        }
    }
}