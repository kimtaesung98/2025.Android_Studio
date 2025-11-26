package com.example.babful.ui.owner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.babful.data.model.Order
import com.example.babful.data.repository.OwnerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OwnerOrderUiState(
    val isLoading: Boolean = false,
    val orders: List<Order> = emptyList()
)

@HiltViewModel
class OwnerOrderViewModel @Inject constructor(
    private val repository: OwnerRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OwnerOrderUiState())
    val uiState = _uiState.asStateFlow()

    fun loadOrders() {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            try {
                val orders = repository.getOrders()
                _uiState.update { it.copy(isLoading = false, orders = orders) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun updateStatus(orderId: Int, status: String) {
        viewModelScope.launch {
            repository.updateOrderStatus(orderId, status)
            loadOrders() // 상태 변경 후 목록 갱신
        }
    }
}