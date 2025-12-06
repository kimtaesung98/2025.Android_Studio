package com.example.deliveryapp2.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.deliveryapp2.data.model.MenuItem
import com.example.deliveryapp2.data.repository.CartRepository
import com.example.deliveryapp2.data.repository.NetworkDeliveryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StoreDetailViewModel(
    private val repository: NetworkDeliveryRepository,
    private val storeId: String

) : ViewModel() {

    private val _menuItems = MutableStateFlow<List<MenuItem>>(emptyList())
    val menuItems = _menuItems.asStateFlow()

    init {
        loadMenus()
    }

    private fun loadMenus() {
        viewModelScope.launch {
            try {
                // Repository에 getMenus 함수가 없다면 빨간줄 -> 아래 단계에서 추가
                val menus = repository.getMenus(storeId)
                _menuItems.value = menus
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun addToCart(menu: MenuItem) {
        CartRepository.addToCart(menu)
    }
}

// storeId를 파라미터로 받기 위한 팩토리
class StoreDetailViewModelFactory(
    private val repository: NetworkDeliveryRepository,
    private val storeId: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return StoreDetailViewModel(repository, storeId) as T
    }
}