package com.example.deliveryapp2.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.deliveryapp2.data.model.Order
import com.example.deliveryapp2.data.repository.NetworkDeliveryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CustomerOrderViewModel(private val repository: NetworkDeliveryRepository) : ViewModel() {

    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders = _orders.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    init {
        loadMyOrders()
    }

    fun loadMyOrders() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Repository의 getOrders() 호출 (이전에 중복 제거하며 정리한 함수)
                val result = repository.getOrders()
                _orders.value = result
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}

class CustomerOrderViewModelFactory(private val repository: NetworkDeliveryRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CustomerOrderViewModel(repository) as T
    }
}