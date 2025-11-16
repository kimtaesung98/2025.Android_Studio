package com.example.babful.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.babful.data.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// 1. Splash 화면의 UI 상태
data class SplashUiState(
    val isLoading: Boolean = true,
    val isLoggedIn: Boolean = false // ⭐️ 토큰 유무 (자동 로그인)
)

@HiltViewModel
class MainViewModel @Inject constructor(
    private val prefsRepo: UserPreferencesRepository // ⭐️ 31단계의 DataStore 래퍼
) : ViewModel() {

    private val _uiState = MutableStateFlow(SplashUiState())
    val uiState = _uiState.asStateFlow()

    init {
        Log.d("MainViewModel", "자동 로그인 검사 시작...")
        checkLoginStatus()
    }

    // 2. DataStore에서 JWT 토큰을 확인하는 함수
    private fun checkLoginStatus() {
        viewModelScope.launch {
            // DataStore에서 토큰을 '한 번' 읽어옴
            val token = prefsRepo.getJwtTokenOnce()

            // ⭐️ 토큰이 비어있지 않으면 '로그인' 상태, 비어있으면 '비로그인' 상태
            val isLoggedIn = !token.isNullOrBlank()

            Log.d("MainViewModel", "토큰 확인 완료. isLoggedIn = $isLoggedIn")

            _uiState.update {
                it.copy(
                    isLoading = false, // ⭐️ 로딩 완료
                    isLoggedIn = isLoggedIn // ⭐️ 상태 확정
                )
            }
        }
    }
}