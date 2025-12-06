package com.example.deliveryapp2.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.deliveryapp2.data.model.DashboardStats
import com.example.deliveryapp2.data.repository.NetworkDeliveryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class OwnerDashboardViewModel(private val repository: NetworkDeliveryRepository) : ViewModel() {

    // 초기값은 null로 두어 로딩 상태를 구분하거나, 0으로 초기화
    private val _stats = MutableStateFlow<DashboardStats?>(null)
    val stats = _stats.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    // 화면이 켜질 때 자동으로 데이터 로드
    init {
        loadStats()
    }

    fun loadStats() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // 🟢 [Real Data] 서버 API 호출
                val result = repository.getDashboardStats()
                _stats.value = result
            } catch (e: Exception) {
                e.printStackTrace()
                // 에러 발생 시 0으로 표시 (앱 죽음 방지)
                _stats.value = DashboardStats(0, 0, 0, 0)
            } finally {
                _isLoading.value = false
            }
        }
    }
}

class OwnerDashboardViewModelFactory(private val repository: NetworkDeliveryRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return OwnerDashboardViewModel(repository) as T
    }
}