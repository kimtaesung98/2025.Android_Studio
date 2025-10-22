package com.example.appname.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope // viewModelScope를 사용하기 위해 import
import kotlinx.coroutines.flow.MutableSharedFlow // SharedFlow import
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow // SharedFlow import
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch // Coroutine launch import
import com.example.appname.model.DeliveryRequest // 입력받은 데이터 저장, 데이터 클라스
// (1) UI 상태를 담을 데이터 클래스
data class DeliveryUiState(
    val restaurantName: String = "",
    val menu: String = "",
    val deliveryAddress: String = ""
)

// (2) AndroidX의 ViewModel을 상속받는 클래스
class DeliveryViewModel : ViewModel() {

    // (3) UI 상태를 외부에는 읽기 전용(StateFlow)으로, 내부에서는 수정 가능(MutableStateFlow)하도록 노출
    private val _uiState = MutableStateFlow(DeliveryUiState())
    val uiState = _uiState.asStateFlow()

    // (1) UI 이벤트를 위한 SharedFlow 추가
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
        if (currentState.restaurantName.isBlank() || currentState.menu.isBlank() || currentState.deliveryAddress.isBlank()) {
            sendEvent("모든 항목을 입력해주세요.")
            return
        }

        // (2) UiState를 DeliveryRequest 모델로 변환
        val requestData = DeliveryRequest(
            restaurant = currentState.restaurantName,
            menu = currentState.menu,
            address = currentState.deliveryAddress
        )

        // TODO: 실제 서버에 'requestData'를 전송하는 로직
        println("요청 데이터 생성 완료: ${requestData}")

        sendEvent("요청이 성공적으로 접수되었습니다.")
    }
    private fun sendEvent(message: String) {
        // (5) 이벤트는 코루틴 스코프에서 발생시켜야 함
        viewModelScope.launch {
            _eventFlow.emit(message)
        }
    }

}