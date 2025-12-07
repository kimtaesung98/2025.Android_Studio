package com.example.deliveryapp2.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.deliveryapp2.data.model.MenuItem
import com.example.deliveryapp2.data.repository.NetworkDeliveryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MenuViewModel(private val repository: NetworkDeliveryRepository) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _isSuccess = MutableStateFlow(false)
    val isSuccess = _isSuccess.asStateFlow()

    fun resetSuccess() {
        _isSuccess.value = false
    }

    fun addMenu(name: String, priceStr: String, description: String, imageUrl: String) {
        if (name.isBlank() || priceStr.isBlank()) return

        viewModelScope.launch {
            _isLoading.value = true
            try {
                // 가격 문자열을 숫자로 변환
                val price = priceStr.toIntOrNull() ?: 0

                // TODO: StoreID는 현재 로그인한 점주의 매장 ID를 써야 하지만,
                // 일단 "1" (Burger King)으로 고정해서 테스트합니다.
                val newMenu = MenuItem(
                    id = "", // 서버에서 생성
                    storeId = "1",
                    name = name,
                    price = price,
                    description = description,
                    imageUrl = imageUrl.ifBlank { "" }
                )

                val success = repository.addMenu(newMenu)
                _isSuccess.value = success
            } catch (e: Exception) {
                e.printStackTrace()
                _isSuccess.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }
}

class MenuViewModelFactory(private val repository: NetworkDeliveryRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MenuViewModel(repository) as T
    }
}