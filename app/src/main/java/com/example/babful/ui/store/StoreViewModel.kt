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
import kotlin.math.min // ⭐️ [신규] (비즈니스 로직)

// ⭐️ [수정] UI 상태 (메뉴 리스트, 장바구니, 사용 포인트 추가)
data class StoreUiState(
    val isLoading: Boolean = true,
    val storeInfo: StoreInfo? = null,
    val user: User? = null,
    val menus: List<Menu> = emptyList(), // ⭐️ [신규] 메뉴 목록
    val cartItems: List<Menu> = emptyList(), // ⭐️ [신규] 장바구니 (선택한 메뉴들)
    val totalPrice: Int = 0, // ⭐️ [신규] 총 주문 금액 (장바구니 합계)
    val pointsUsed: Int = 0, // ⭐️ [추가] 이 주문에 사용한 포인트
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
    
    // ⭐️ [수정] 장바구니 비우기 (포인트 사용내역 포함)
    fun clearCart() {
        _uiState.update { it.copy(cartItems = emptyList(), totalPrice = 0, pointsUsed = 0) }
    }

    // ⭐️ [수정] 결제 로직 (실제 결제 금액 계산)
    fun completeOrder() { 
        val state = _uiState.value
        val amountPaid = state.totalPrice - state.pointsUsed

        // 장바구니가 비어있으면 결제 방지
        if (state.totalPrice <= 0) {
            Log.d("StoreViewModel", "장바구니가 비어있어 결제를 진행할 수 없습니다.")
            return
        }
        // 포인트 사용액이 주문 금액보다 큰 경우 방지 (usePointsForOrder에서 처리되지만, 최종 방어)
        if (amountPaid < 0) {
            Log.e("StoreViewModel", "결제 금액이 0보다 작습니다. 로직 확인 필요.")
            return
        }

        _uiState.update { it.copy(isLoading = true) }
        val orderId = "ORD-${java.util.UUID.randomUUID()}"

        viewModelScope.launch {
            try {
                // ⭐️ [수정] 포인트가 차감된 최종 금액으로 결제
                storeRepository.processPayment(storeId.toInt(), amountPaid, orderId)
                val updatedUser = profileRepository.getProfileInfo()

                // ⭐️ [수정] storeId 전달 (String -> Int 변환 주의)
                // (실제로는 storeId가 숫자로 관리되므로 toInt() 사용)
                storeRepository.processPayment(storeId.toInt(), amountPaid, orderId)

                _uiState.update { it.copy(isLoading = false, user = updatedUser) }
                clearCart() // ⭐️ 결제 후 장바구니 및 사용 포인트 비우기
                Log.d("StoreViewModel", "주문 성공! 금액: $amountPaid")
            } catch (e: Exception) {
                 Log.e("StoreViewModel", "주문 처리 실패", e)
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
}
