package com.example.babful.ui.owner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.babful.data.model.Menu
import com.example.babful.data.repository.OwnerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OwnerMenuUiState(
    val isLoading: Boolean = false,
    val menus: List<Menu> = emptyList()
)

@HiltViewModel
class OwnerMenuViewModel @Inject constructor(
    private val repository: OwnerRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OwnerMenuUiState())
    val uiState = _uiState.asStateFlow()

    fun loadMenus(storeId: Int) {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            try {
                val menus = repository.getMenus(storeId)
                _uiState.update { it.copy(isLoading = false, menus = menus) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun createMenu(storeId: Int, name: String, priceStr: String) {
        val price = priceStr.toIntOrNull() ?: return
        viewModelScope.launch {
            repository.createMenu(storeId, name, price)
            loadMenus(storeId) // 추가 후 목록 갱신
        }
    }
}