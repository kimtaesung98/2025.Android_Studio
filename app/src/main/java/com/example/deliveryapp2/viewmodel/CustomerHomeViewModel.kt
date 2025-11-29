package com.example.deliveryapp2.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.deliveryapp2.data.model.Store
import com.example.deliveryapp2.data.repository.DeliveryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CustomerHomeViewModel(private val repository: DeliveryRepository) : ViewModel() {
    private val _stores = MutableStateFlow<List<Store>>(emptyList())
    val stores: StateFlow<List<Store>> = _stores

    init {
        loadStores()
    }

    private fun loadStores() {
        viewModelScope.launch {
            _stores.value = repository.getStores()
        }
    }
}