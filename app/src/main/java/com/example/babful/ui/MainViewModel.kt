package com.example.babful.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.babful.data.model.ActiveOrder
import com.example.babful.data.repository.ProfileRepository
import com.example.babful.data.store.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MainUiState(
    val isLoading: Boolean = true,
    val isLoggedIn: Boolean = false,
    val activeOrder: ActiveOrder? = null // ⭐️ 활성 주문 상태 추가
)

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userPreferences: UserPreferences,
    private val profileRepository: ProfileRepository // ⭐️ 레포지토리 추가 주입
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState = _uiState.asStateFlow()

    init {
        checkLoginStatus()
    }

    private fun checkLoginStatus() {
        viewModelScope.launch {
            val token = userPreferences.getAuthTokenOnce()
            val isLoggedIn = !token.isNullOrBlank()

            _uiState.update { it.copy(isLoading = false, isLoggedIn = isLoggedIn) }

            // ⭐️ 로그인 상태라면 진행 중인 주문 확인
            if (isLoggedIn) {
                checkActiveOrder()
            }
        }
    }

    // ⭐️ 진행 중인 주문 확인 (폴링 대신 일단 1회 호출, 필요시 반복 호출 가능)
    fun checkActiveOrder() {
        viewModelScope.launch {
            try {
                val order = profileRepository.getActiveOrder()
                _uiState.update { it.copy(activeOrder = order) }
                Log.d("MainVM", "Active Order: $order")
            } catch (e: Exception) {
                _uiState.update { it.copy(activeOrder = null) }
            }
        }
    }

    // (화면 이동 시마다 갱신하고 싶다면 이 함수를 UI에서 호출)
}