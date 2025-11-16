package com.example.babful.ui.store

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.babful.data.model.StoreInfo
import com.example.babful.data.model.User
import com.example.babful.data.repository.ProfileRepository
import com.example.babful.data.repository.StoreRepository
import com.example.babful.ui.NavigationRoutes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import java.util.UUID // ⭐️ [신규] (가상 order_id)

// ⭐️ [수정] 1. UI 상태 (pointsUsed 제거)
data class StoreUiState(
    val isLoading: Boolean = true,
    val storeInfo: StoreInfo? = null,
    val user: User? = null, // (내 포인트 잔액)
    // ⭐️ [제거] val pointsUsed: Int = 0,
    val error: String? = null
)

@HiltViewModel
class StoreViewModel @Inject constructor(
    private val storeRepository: StoreRepository,
    private val profileRepository: ProfileRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(StoreUiState())
    val uiState = _uiState.asStateFlow()

    // ⭐️ 1. '쇼츠' 탭에서 클릭한 storeId (예: "store_1")
    private val storeId: String = checkNotNull(savedStateHandle[NavigationRoutes.ARG_STORE_ID])

    init {
        loadStoreData()
    }

    // 2. '가게 정보' + '내 정보' 동시 로드 (39단계와 동일)
    fun loadStoreData() {
        Log.d("StoreViewModel", "가게/프로필 정보 로드 시작 (ID: $storeId)")
        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            try {
                val storeInfo = storeRepository.getStoreInfo(storeId)
                val user = profileRepository.getProfileInfo()
                _uiState.update {
                    it.copy(isLoading = false, storeInfo = storeInfo, user = user)
                }
            } catch (e: Exception) {
                Log.e("StoreViewModel", "가게/프로필 정보 로드 실패", e)
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
                    storeRepository.unsubscribeStore(storeId)
                    Log.d("StoreViewModel", "[토글] '구독 취소' 백엔드 요청 성공")
                } else {
                    storeRepository.subscribeStore(storeId)
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
    }// ⭐️ [수정] 3. '포인트 사용' -> '결제/적립' 로직
    fun completeOrder(amountPaid: Int) {
        _uiState.update { it.copy(isLoading = true) } // (결제 로딩)

        // (가상 결제 PG 연동...)

        // ⭐️ '가상' 주문 ID 생성 (악용 방지 테스트용)
        val orderId = "ORD-${UUID.randomUUID()}"
        Log.d("StoreViewModel", "가상 결제 성공. [적립 요청] OrderID: $orderId, Amount: $amountPaid")

        viewModelScope.launch {
            try {
                // 1. (API) Go 서버에 '결제/적립' 요청
                storeRepository.processPayment(amountPaid, orderId)

                // 2. (API) '내 정보' (포인트 잔액) 새로고침
                val updatedUser = profileRepository.getProfileInfo()

                // 3. (UI) UI 상태 갱신
                _uiState.update {
                    it.copy(isLoading = false, user = updatedUser) // ⭐️ 잔액 갱신
                }
                Log.d("StoreViewModel", "포인트 적립 성공. 새 잔액: ${updatedUser.points}")
            } catch (e: Exception) {
                Log.e("StoreViewModel", "포인트 적립/갱신 실패", e)
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }
}