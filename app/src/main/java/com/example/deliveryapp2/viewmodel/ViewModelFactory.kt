// File: /viewmodel/ViewModelFactory.kt
package com.example.deliveryapp2.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.deliveryapp2.data.repository.DeliveryRepository
import com.example.deliveryapp2.data.repository.StoreRepository

class CustomerHomeViewModelFactory(private val repository: DeliveryRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CustomerHomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CustomerHomeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class OwnerOrderViewModelFactory(private val repository: DeliveryRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OwnerOrderViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return OwnerOrderViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class HomeViewModelFactory(private val repository: StoreRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}