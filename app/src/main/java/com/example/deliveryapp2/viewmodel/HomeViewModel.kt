// File: /viewmodel/HomeViewModel.kt
package com.example.deliveryapp2.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.deliveryapp2.data.model.Store
import com.example.deliveryapp2.data.repository.StoreRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HomeUiState(
    val isLoading: Boolean = false,
    val categories: List<String> = listOf("All", "Burgers", "Pizza", "Korean", "Dessert"),
    val storeList: List<Store> = emptyList()
)

class HomeViewModel(private val repository: StoreRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val stores = repository.getStoreList()
            _uiState.value = _uiState.value.copy(isLoading = false, storeList = stores)
        }
    }
}