package com.example.babful.ui.delivery

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.babful.data.model.DeliveryItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

// 1. ViewModel이 UI에 전달할 화면 상태 (State)
data class DeliveryUiState(
    val deliveryItems: List<DeliveryItem> = emptyList(),
    val isLoading: Boolean = false
)

// 2. ViewModel 클래스 정의 (FeedViewModel과 구조 동일)
class DeliveryViewModel : ViewModel() {

    // 3. UI 상태를 관리하는 StateFlow
    private val _uiState = MutableStateFlow(DeliveryUiState())
    val uiState: StateFlow<DeliveryUiState> = _uiState.asStateFlow()

    // 4. ViewModel이 생성(초기화)될 때 데이터 로드
    init {
        Log.d("DeliveryViewModel", "ViewModel이 생성되었습니다.")
        loadDeliveryOrders()
    }

    // 5. 데이터 로딩 (7단계의 가짜 데이터 생성 로직이 여기로 이동)
    private fun loadDeliveryOrders() {
        val fakeDeliveryItems = (1..30).map { i ->
            // ⭐️ 가게 이미지 URL 생성
            val storeImgUrl = "https://picsum.photos/seed/store_$i/200/200"

            DeliveryItem(
                id = UUID.randomUUID().toString(),
                storeName = "VM-맛있는 가게 #$i",
                storeImageUrl = storeImgUrl, // ⭐️ [수정] storeImageUrl에 URL 할당
                estimatedTimeInMinutes = (10..60).random(),
                status = if (i % 3 == 0) "배달중" else "조리중"
            )
        }
        _uiState.update { it.copy(deliveryItems = fakeDeliveryItems) }
    }
}