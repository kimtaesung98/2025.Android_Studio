package com.example.babful.ui.delivery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.babful.data.model.ActiveOrder
import com.example.babful.data.model.DeliveryItem
import com.example.babful.data.repository.DeliveryRepository
import com.example.babful.data.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DeliveryUiState(
    val isLoading: Boolean = false,
    val deliveryItems: List<DeliveryItem> = emptyList(), // 가게 목록
    val activeOrder: ActiveOrder? = null,                // 현재 진행 중인 주문
    val errorMessage: String? = null
)

@HiltViewModel
class DeliveryViewModel @Inject constructor(
    private val deliveryRepository: DeliveryRepository,
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DeliveryUiState())
    val uiState: StateFlow<DeliveryUiState> = _uiState.asStateFlow()

    init {
        loadData()
        startStatusPolling() // 실시간 상태 확인 시작
    }

    // 데이터 초기 로드
    private fun loadData() {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            try {
                // 1. 가게 목록 로드
                val items = deliveryRepository.getDeliveryItemsFromNetwork()
                // 2. 현재 주문 상태 로드
                val order = profileRepository.getActiveOrder()

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        deliveryItems = items,
                        activeOrder = order
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    // ⭐️ 5초마다 주문 상태 갱신 (실시간성 확보)
    private fun startStatusPolling() {
        viewModelScope.launch {
            while (true) {
                try {
                    val order = profileRepository.getActiveOrder()
                    _uiState.update { it.copy(activeOrder = order) }
                } catch (_: Exception) { }
                delay(5000) // 5초 대기
            }
        }
    }

    // 수동 새로고침
    fun refresh() {
        loadData()
    }
}