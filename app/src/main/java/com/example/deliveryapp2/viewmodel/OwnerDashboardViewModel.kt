package com.example.deliveryapp2.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.deliveryapp2.data.model.DashboardStats
import com.example.deliveryapp2.data.repository.DeliveryRepository // Repository 이름 확인 필요
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class OwnerDashboardViewModel(private val repository: com.example.deliveryapp2.data.repository.NetworkDeliveryRepository) : ViewModel() {

    private val _stats = MutableStateFlow<DashboardStats?>(null)
    val stats = _stats.asStateFlow()

    fun loadStats() {
        viewModelScope.launch {
            try {
                // 1. 정상적으로 데이터 요청
                val result = repository.getDashboardStats()
                _stats.value = result
            } catch (e: Exception) {
                e.printStackTrace()
                // 🚨 [수정] 에러 나면 '0'으로 채워진 데이터라도 보여줌 (무한 로딩 방지)
                _stats.value = DashboardStats(0, 0, 0, 0)
            }
        }
    }
}

// Factory
class OwnerDashboardViewModelFactory(private val repository: com.example.deliveryapp2.data.repository.NetworkDeliveryRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OwnerDashboardViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return OwnerDashboardViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}