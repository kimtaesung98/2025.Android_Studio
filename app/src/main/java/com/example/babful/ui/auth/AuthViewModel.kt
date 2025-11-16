package com.example.babful.ui.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope // ⭐️ [신규]
import com.example.babful.data.repository.AuthRepository // ⭐️ [신규]
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch // ⭐️ [신규]
import javax.inject.Inject

data class AuthUiState(
    val email: String = "",
    val pass: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val navigateToFeed: Boolean = false, // ⭐️ [신규] 네비게이션 트리거
    val navigateToLogin: Boolean = false // ⭐️ [신규] 네비게이션 트리거
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository // ⭐️ [신규] AuthRepository 주입
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState = _uiState.asStateFlow()

    fun onEmailChange(email: String) { _uiState.update { it.copy(email = email) } }
    fun onPasswordChange(pass: String) { _uiState.update { it.copy(pass = pass) } }

    // ⭐️ [신규] 네비게이션 이벤트가 '소비'되었음을 VM에 알림
    fun onNavigationDone() {
        _uiState.update { it.copy(navigateToFeed = false, navigateToLogin = false) }
    }

    // ⭐️ [수정] 5. '로그인' 버튼 클릭 시
    fun login() {
        Log.d("AuthViewModel", "로그인 시도: Email = ${uiState.value.email}")
        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            try {
                // 1. Repository 호출
                repository.login(uiState.value.email, uiState.value.pass)
                // 2. 성공 시, UI에 '네비게이션' 이벤트 전달
                _uiState.update { it.copy(isLoading = false, navigateToFeed = true) }
            } catch (e: Exception) {
                // 3. 실패 시, UI에 '에러' 전달
                Log.e("AuthViewModel", "로그인 실패", e)
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    // ⭐️ [수정] 6. '회원가입' 버튼 클릭 시
    fun register() {
        Log.d("AuthViewModel", "회원가입 시도: Email = ${uiState.value.email}")
        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            try {
                // 1. Repository 호출
                repository.register(uiState.value.email, uiState.value.pass)
                // 2. 성공 시, UI에 '로그인 화면 이동' 이벤트 전달
                _uiState.update { it.copy(isLoading = false, navigateToLogin = true) }
            } catch (e: Exception) {
                // 3. 실패 시, UI에 '에러' 전달 (예: "Email already exists")
                Log.e("AuthViewModel", "회원가입 실패", e)
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
}