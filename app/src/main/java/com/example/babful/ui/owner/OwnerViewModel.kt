package com.example.babful.ui.owner

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.babful.data.model.Order
import com.example.babful.data.model.OwnerStore
import com.example.babful.data.repository.OwnerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OwnerUiState(
    val isLoading: Boolean = false,
    val myStore: OwnerStore? = null,
    val isStoreCreated: Boolean = false, // 등록 성공 시 true
    val error: String? = null,
    val orders: List<Order> = emptyList() // ⭐️ [신규]
)

@HiltViewModel
class OwnerViewModel @Inject constructor(
    private val repository: OwnerRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(OwnerUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadMyStore()
    }

    fun loadMyStore() {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            try {
                val store = repository.getMyStore()
                _uiState.update { it.copy(isLoading = false, myStore = store) }
            } catch (e: Exception) {
                // 404(가게 없음)도 에러로 올 수 있음 -> 등록 화면 보여주기 위해 무시 가능
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun createStore(name: String, desc: String) {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            try {
                // (임시 좌표: 강남역 근처)
                repository.createStore(name, desc, 37.4980, 127.0277)
                _uiState.update { it.copy(isLoading = false, isStoreCreated = true) }
                loadMyStore() // 등록 후 새로고침
            } catch (e: Exception) {
                Log.e("OwnerVM", "가게 등록 실패", e)
                _uiState.update { it.copy(isLoading = false, error = "등록 실패") }
            }
        }
    }
    // ⭐️ [신규] 주문 목록 로드
    fun loadOrders() {
        viewModelScope.launch {
            try {
                val orders = repository.getOrders()
                _uiState.update { it.copy(orders = orders) }
            } catch (e: Exception) { /* 에러 처리 */ }
        }
    }
}