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

// ⭐️ [수정] isLoading 상태 추가 (UI 스피너 연동용)
data class DeliveryUiState(
    val deliveryItems: List<DeliveryItem> = emptyList(),
    val isLoading: Boolean = false
)

// ⭐️ [수정] @HiltViewModel 어노테이션 추가
@HiltViewModel
class DeliveryViewModel @Inject constructor( // ⭐️ [수정] 생성자에 @Inject 및 Repository 추가
    private val repository: DeliveryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DeliveryUiState())
    val uiState: StateFlow<DeliveryUiState> = _uiState.asStateFlow()

    init {
        Log.d("DeliveryViewModel", "ViewModel이 생성(주입)되었습니다.")
        loadDeliveryOrders() // ⭐️ [수정] 함수 이름 유지
    }

    // ⭐️ [수정] 데이터 로딩 로직을 Repository 호출로 변경
    private fun loadDeliveryOrders() {
        Log.d("DeliveryViewModel", "Repository에 배달 목록 요청")

        // 1. 로딩 상태 시작
        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            // 2. Repository에서 데이터 가져오기 (delay는 Repo가 담당)
            val items = repository.getDeliveryItems()

            // 3. UI 상태 업데이트
            _uiState.update {
                it.copy(
                    isLoading = false,
                    deliveryItems = items
                )
            }
            Log.d("DeliveryViewModel", "Repository로부터 응답 받음")
        }
    }

    // ⭐️ [제거] 9단계의 'loadDeliveryOrders' 내부 로직(가짜 데이터 생성)은 삭제됨
}