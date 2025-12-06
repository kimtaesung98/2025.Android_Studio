package com.example.deliveryapp2.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.deliveryapp2.data.model.Store
import com.example.deliveryapp2.data.repository.NetworkDeliveryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StoreListViewModel(private val repository: NetworkDeliveryRepository) : ViewModel() {

    // 1. 전체 매장 리스트 (서버에서 받아온 원본 보관용)
    private var allStores = listOf<Store>()

    // 2. 화면에 실제로 보여줄 리스트 (검색 필터링 결과)
    private val _stores = MutableStateFlow<List<Store>>(emptyList())
    val stores = _stores.asStateFlow()

    // 3. 현재 검색어
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    init {
        loadStores()
    }

    fun loadStores() {
        viewModelScope.launch {
            try {
                // 서버에서 데이터 가져오기
                val result = repository.getStores()
                allStores = result // 원본 저장

                // 현재 검색어로 필터링하여 화면에 반영 (처음엔 검색어가 없으니 다 보여줌)
                filterStores(_searchQuery.value)
            } catch (e: Exception) {
                e.printStackTrace()
                _stores.value = emptyList() // 에러 시 빈 리스트
            }
        }
    }

    // 검색어가 바뀔 때마다 호출되는 함수
    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        filterStores(query)
    }

    // 실제 필터링 로직 (대소문자 무시하고 검색)
    private fun filterStores(query: String) {
        if (query.isBlank()) {
            _stores.value = allStores
        } else {
            _stores.value = allStores.filter {
                it.name.contains(query, ignoreCase = true)
            }
        }
    }
}

// 팩토리는 기존과 동일
class StoreListViewModelFactory(private val repository: NetworkDeliveryRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StoreListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StoreListViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}