package com.example.appname.delivery.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appname.delivery.domain.model.DeliveryRequest
import com.example.appname.delivery.domain.usecase.SubmitDeliveryRequestUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

import dagger.hilt.android.lifecycle.HiltViewModel // 🚨 (1)
import javax.inject.Inject // 🚨 (1)
// (1) UI 상태를 담을 데이터 클래스
data class DeliveryUiState(
    val restaurantName: String = "",
    val menu: String = "",
    val deliveryAddress: String = ""
)

// (2) AndroidX의 ViewModel을 상속받는 클래스
@HiltViewModel
class DeliveryViewModel @Inject constructor( // (3) 🚨 생성자에 @Inject 추가
    private val submitDeliveryRequestUseCase: SubmitDeliveryRequestUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(DeliveryUiState())
    val uiState = _uiState.asStateFlow()

    private val _eventFlow = MutableSharedFlow<String>()
    val eventFlow = _eventFlow.asSharedFlow()

    // (4) UI로부터 이벤트를 받아 상태를 변경하는 함수들
    fun onRestaurantNameChange(name: String) {
        _uiState.update { currentState ->
            currentState.copy(restaurantName = name)
        }
    }

    fun onMenuChange(menu: String) {
        _uiState.update { currentState ->
            currentState.copy(menu = menu)
        }
    }

    fun onDeliveryAddressChange(address: String) {
        _uiState.update { currentState ->
            currentState.copy(deliveryAddress = address)
        }
    }

    fun submitDeliveryRequest() {
        val currentState = uiState.value

        // 🚨 (3) 1단계의 유효성 검사 로직이 UseCase로 이동했으므로 여기서는 제거됨.

        // (4) UiState를 Domain Model(DeliveryRequest)로 변환
        val requestData = DeliveryRequest(
            restaurant = currentState.restaurantName,
            menu = currentState.menu,
            address = currentState.deliveryAddress
            // requestTime 등은 UseCase나 Repository가 설정할 수 있음
        )

        // 🚨 (5) UseCase(suspend 함수)를 viewModelScope에서 호출
        viewModelScope.launch {
            val result = submitDeliveryRequestUseCase(requestData) // UseCase 호출

            result.onSuccess {
                // (6) 성공 시 UI 이벤트 발생
                sendEvent("요청이 성공적으로 접수되었습니다.")
                // TODO: 2단계 심화 - 요청 성공 시 입력 필드 초기화
            }
            result.onFailure { exception ->
                // (7) 실패 시 UI 이벤트 발생
                sendEvent(exception.message ?: "알 수 없는 오류가 발생했습니다.")
            }
        }
    }

    private fun sendEvent(message: String) {
        viewModelScope.launch {
            _eventFlow.emit(message)
        }
    }
}