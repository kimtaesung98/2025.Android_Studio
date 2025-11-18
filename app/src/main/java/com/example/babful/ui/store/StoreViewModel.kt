package com.example.babful.ui.store

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.babful.data.model.StoreInfo
import com.example.babful.data.model.User // ⭐️ [신규]
import com.example.babful.data.repository.ProfileRepository // ⭐️ [신규]
import com.example.babful.data.repository.StoreRepository
import com.example.babful.ui.NavigationRoutes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import java.util.UUID // ⭐️ [신규] (가상 order_id)
import com.example.babful.data.model.Menu // ⭐️ [신규]

// ⭐️ [수정] UI 상태 (메뉴 리스트, 장바구니 추가)
data class StoreUiState(
    val isLoading: Boolean = true,
    val storeInfo: StoreInfo? = null,
    val user: User? = null,
    val menus: List<Menu> = emptyList(), // ⭐️ [신규] 메뉴 목록
    val cartItems: List<Menu> = emptyList(), // ⭐️ [신규] 장바구니 (선택한 메뉴들)
    val totalPrice: Int = 0, // ⭐️ [신규] 총 주문 금액 (장바구니 합계)
    val error: String? = null
)

@HiltViewModel
class StoreViewModel @Inject constructor(
    private val storeRepository: StoreRepository,
    private val profileRepository: ProfileRepository, // ⭐️ [신규] ProfileRepository 주입
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(StoreUiState())
    val uiState = _uiState.asStateFlow()

    private val storeId: String = checkNotNull(savedStateHandle[NavigationRoutes.ARG_STORE_ID])

    init {
        loadStoreData() // ⭐️ [수정] (함수 이름 변경)
    }

    // ⭐️ [수정] 데이터 로드 (메뉴 포함)
    fun loadStoreData() {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            try {
                val storeInfo = storeRepository.getStoreInfo(storeId)
                val user = profileRepository.getProfileInfo()
                // ⭐️ [신규] 메뉴 로드
                val menus = storeRepository.getMenus(storeId)

                _uiState.update { 
                    it.copy(
                        isLoading = false, 
                        storeInfo = storeInfo, 
                        user = user,
                        menus = menus // ⭐️
                    ) 
                }
            } catch (e: Exception) {
                Log.e("StoreViewModel", "가게/프로필/메뉴 정보 로드 실패", e)
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
                    it.copy(storeInfo = currentStore) // ⭐️ 원상 복구
                }
            }
        }
    }

    // ⭐️ [신규] 장바구니 담기
    fun addToCart(menu: Menu) {
        val currentCart = _uiState.value.cartItems + menu
        val currentTotal = currentCart.sumOf { it.price }
        
        _uiState.update { 
            it.copy(
                cartItems = currentCart,
                totalPrice = currentTotal
            ) 
        }
    }
    
    // ⭐️ [신규] 장바구니 비우기 (초기화)
    fun clearCart() {
        _uiState.update { it.copy(cartItems = emptyList(), totalPrice = 0) }
    }

    // ⭐️ [수정] 결제 로직 (고정값 10000원 -> 실제 장바구니 금액)
    fun completeOrder() { // 인자 제거 (totalPrice 사용)
        val amountPaid = _uiState.value.totalPrice
        if (amountPaid <= 0) return // 0원 결제 방지

        _uiState.update { it.copy(isLoading = true) }
        // ... (UUID 생성 등 기존 로직) ...
        val orderId = "ORD-${java.util.UUID.randomUUID()}"

        viewModelScope.launch {
            try {
                storeRepository.processPayment(amountPaid, orderId) // ⭐️ 실제 금액 사용
                val updatedUser = profileRepository.getProfileInfo()
                
                _uiState.update { it.copy(isLoading = false, user = updatedUser) }
                clearCart() // ⭐️ 결제 후 장바구니 비우기
                Log.d("StoreViewModel", "주문 성공! 금액: $amountPaid")
            } catch (e: Exception) {
                 Log.e("StoreViewModel", "주문 처리 실패", e)
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
}