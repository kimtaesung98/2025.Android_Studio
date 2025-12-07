package com.example.deliveryapp2.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.deliveryapp2.data.local.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ProfileViewModel(private val context: Context) : ViewModel() {
    private val tokenManager = TokenManager(context)

    private val _userInfo = MutableStateFlow(mapOf<String, String>())
    val userInfo = _userInfo.asStateFlow()

    init {
        loadUserInfo()
    }

    private fun loadUserInfo() {
        _userInfo.value = mapOf(
            "name" to (tokenManager.getUserName() ?: "Unknown"),
            "role" to (tokenManager.getUserRole() ?: "Guest"),
            "address" to tokenManager.getUserAddress()
        )
    }

    fun updateAddress(newAddress: String) {
        tokenManager.saveUserAddress(newAddress)
        loadUserInfo() // UI 갱신
    }

    fun logout() {
        tokenManager.clearToken()
    }
}

class ProfileViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ProfileViewModel(context) as T
    }
}