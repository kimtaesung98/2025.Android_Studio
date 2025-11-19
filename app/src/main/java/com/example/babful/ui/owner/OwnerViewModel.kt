package com.example.babful.ui.owner

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.babful.data.model.Menu
import com.example.babful.data.model.Order // ⭐️ [신규] Order 모델 임포트
import com.example.babful.data.model.OwnerStore
import com.example.babful.data.repository.OwnerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// ⭐️ [수정] UI 상태에 메뉴/주문 리스트 추가
data class OwnerUiState(
    val isLoading: Boolean = false,
    val myStore: OwnerStore? = null,
    val isStoreCreated: Boolean = false,
    val menus: List<Menu> = emptyList(),
    val orders: List<Order> = emptyList(), // ⭐️ [신규] 주문 목록 추가
    val error: String? = null
)

@HiltViewModel
class OwnerViewModel @Inject constructor(
    private val repository: OwnerRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(OwnerUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadMyStore()
        loadOrders() // ⭐️ [신규] 초기화 시 주문 목록도 로드
    }

    fun loadMyStore() {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            try {
                val store = repository.getMyStore()
                _uiState.update { it.copy(isLoading = false, myStore = store) }
                // ⭐️ 가게 정보 로드 성공 시, 메뉴도 로드
                store?.id?.let { loadMenus(it) }
            } catch (e: Exception) {
                // 404(가게 없음)는 정상 케이스이므로 에러 메시지 표시 안 함
                _uiState.update { it.copy(isLoading = false, error = null, myStore = null) }
            }
        }
    }

    fun loadMenus(storeId: Int) {
        viewModelScope.launch {
            try {
                val menus = repository.getMenus(storeId)
                _uiState.update { it.copy(menus = menus) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "메뉴 로드 실패") }
            }
        }
    }

    fun createMenu(name: String, priceString: String) {
        val storeId = _uiState.value.myStore?.id ?: return
        val price = priceString.toIntOrNull() ?: return

        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            try {
                repository.createMenu(storeId, name, price)
                loadMenus(storeId) // 성공 시 목록 갱신
                _uiState.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = "메뉴 등록 실패") }
            }
        }
    }

    fun createStore(name: String, desc: String) {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            try {
                repository.createStore(name, desc, 37.4980, 127.0277) // (임시 좌표)
                _uiState.update { it.copy(isLoading = false, isStoreCreated = true) }
                loadMyStore() // 등록 후 가게 정보 새로고침
            } catch (e: Exception) {
                Log.e("OwnerVM", "가게 등록 실패", e)
                _uiState.update { it.copy(isLoading = false, error = "등록 실패") }
            }
        }
    }

    fun loadOrders() {
        viewModelScope.launch {
            try {
                val orders = repository.getOrders()
                _uiState.update { it.copy(orders = orders) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "주문 목록 로드 실패") }
            }
        }
    }

    // ⭐️ [신규] 주문 상태 변경
    fun updateOrderStatus(orderId: Int, newStatus: String) {
        // (낙관적 업데이트는 생략하고, 서버 성공 후 목록 갱신 방식으로 처리)
        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            try {
                repository.updateOrderStatus(orderId, newStatus)
                Log.d("OwnerVM", "주문 상태 변경 성공: $newStatus")
                loadOrders() // ⭐️ 목록 새로고침 (변경된 상태 반영)
            } catch (e: Exception) {
                Log.e("OwnerVM", "주문 상태 변경 실패", e)
                _uiState.update { it.copy(isLoading = false, error = "상태 변경 실패") }
            }
        }
    }
}
