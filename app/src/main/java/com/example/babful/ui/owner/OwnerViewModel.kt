package com.example.babful.ui.owner

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.babful.data.model.OwnerStore
import com.example.babful.data.repository.OwnerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OwnerUiState(
    val isLoading: Boolean = false,
    val myStore: OwnerStore? = null, // 가게 정보 (없으면 null)
    val todaySales: Int = 0,         // 오늘의 매출 (계산됨)
    val pendingOrderCount: Int = 0,  // 대기 중인 주문 수
    val error: String? = null
)

@HiltViewModel
class OwnerViewModel @Inject constructor(
    private val repository: OwnerRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OwnerUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadOwnerData()
    }

    // 초기 데이터 로드 (가게 정보 + 주문 현황)
    fun loadOwnerData() {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            try {
                // 1. 내 가게 조회
                val store = repository.getMyStore()

                // 2. 주문 내역 조회 (매출 계산용)
                val orders = repository.getOrders()

                // 간단한 통계 계산
                val sales = orders.filter { it.status == "배달완료" }.sumOf { it.amount }
                val pending = orders.count { it.status == "접수대기" }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        myStore = store,
                        todaySales = sales,
                        pendingOrderCount = pending
                    )
                }
            } catch (e: Exception) {
                // 404 에러 등은 가게가 없는 것으로 간주
                Log.e("OwnerVM", "가게 조회 실패 (또는 없음): ${e.message}")
                _uiState.update { it.copy(isLoading = false, myStore = null) }
            }
        }
    }

    // 가게 등록
    fun createStore(name: String, desc: String) {
        viewModelScope.launch {
            try {
                repository.createStore(name, desc)
                loadOwnerData() // 등록 후 데이터 다시 로드
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "가게 등록 실패") }
            }
        }
    }
}