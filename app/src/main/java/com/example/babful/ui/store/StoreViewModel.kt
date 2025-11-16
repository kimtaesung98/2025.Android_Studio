package com.example.babful.ui.store

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.babful.data.model.StoreInfo
import com.example.babful.data.repository.StoreRepository
import com.example.babful.ui.NavigationRoutes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class StoreUiState(
    val isLoading: Boolean = true,
    val storeInfo: StoreInfo? = null,
    val error: String? = null
)

@HiltViewModel
class StoreViewModel @Inject constructor(
    private val repository: StoreRepository,
    savedStateHandle: SavedStateHandle // ⭐️ 네비게이션 인자(storeId)를 받기 위함
) : ViewModel() {

    private val _uiState = MutableStateFlow(StoreUiState())
    val uiState = _uiState.asStateFlow()

    // ⭐️ 1. '쇼츠' 탭에서 클릭한 storeId (예: "store_1")
    private val storeId: String = checkNotNull(savedStateHandle[NavigationRoutes.ARG_STORE_ID])

    init {
        loadStoreInfo()
    }

    // 2. '가게 정보' + '구독 여부' 로드
    private fun loadStoreInfo() {
        Log.d("StoreViewModel", "가게 정보 로드 시작 (ID: $storeId)")
        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            try {
                val storeInfo = repository.getStoreInfo(storeId)
                _uiState.update { it.copy(isLoading = false, storeInfo = storeInfo) }
                Log.d("StoreViewModel", "가게 정보 로드 성공. 구독상태: ${storeInfo.isSubscribed}")
            } catch (e: Exception) {
                Log.e("StoreViewModel", "가게 정보 로드 실패", e)
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    // 3. ⭐️ '구독 토글' 로직 (좋아요와 동일)
    fun toggleSubscription() {
        val currentStore = _uiState.value.storeInfo ?: return
        val isSubscribed = currentStore.isSubscribed

        Log.d("StoreViewModel", "[구독 토글] Store ID: $storeId. 현재 상태: $isSubscribed")

        // 1. (Optimistic Update) UI 즉시 갱신
        _uiState.update {
            it.copy(storeInfo = currentStore.copy(isSubscribed = !isSubscribed))
        }

        // 2. (Network Request) 백엔드에 '구독' 또는 '구독 취소' 요청
        viewModelScope.launch {
            try {
                if (isSubscribed) {
                    repository.unsubscribeStore(storeId)
                    Log.d("StoreViewModel", "[토글] '구독 취소' 백엔드 요청 성공")
                } else {
                    repository.subscribeStore(storeId)
                    Log.d("StoreViewModel", "[토글] '구독' 백엔드 요청 성공")
                }
            } catch (e: Exception) {
                // 3. (Rollback) 실패 시 UI 원상 복구
                Log.e("StoreViewModel", "[토글] 백엔드 요청 실패, UI 롤백", e)
                _uiState.update {
                    it.copy(storeInfo = currentStore.copy(isSubscribed = isSubscribed)) // ⭐️ 원상 복구
                }
            }
        }
    }
}