package com.example.babful.ui.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.babful.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthUiState(
    val email: String = "",
    val pass: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isRegisterSuccess: Boolean = false,
    val navigateToFeed: Boolean = false,
    val navigateToLogin: Boolean = false
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState = _uiState.asStateFlow()

    fun onEmailChange(email: String) { _uiState.update { it.copy(email = email) } }
    fun onPasswordChange(pass: String) { _uiState.update { it.copy(pass = pass) } }

    fun onNavigationDone() {
        _uiState.update { it.copy(navigateToFeed = false, navigateToLogin = false) }
    }

    // ⭐️ [수정] 로그인 로직 (Result 처리 방식 적용)
    fun login() {
        val email = uiState.value.email
        val pass = uiState.value.pass

        Log.d("AuthViewModel", "로그인 시도: Email = $email")
        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            // Repository가 Result<Unit>을 반환하므로 try-catch 대신 결과값 확인
            val result = repository.login(email, pass)

            if (result.isSuccess) {
                // 성공 시 Feed로 이동
                _uiState.update { it.copy(isLoading = false, navigateToFeed = true) }
            } else {
                // 실패 시 에러 메시지 표시
                val errorMsg = result.exceptionOrNull()?.message ?: "로그인 실패"
                Log.e("AuthViewModel", "로그인 실패: $errorMsg")
                _uiState.update { it.copy(isLoading = false, error = errorMsg) }
            }
        }
    }

    // ⭐️ [수정] 회원가입 로직 (중복 호출 제거 및 Role 적용)
    fun register(email: String, pass: String, role: String = "customer") {
        if (email.isBlank() || pass.isBlank()) {
            _uiState.update { it.copy(error = "이메일과 비밀번호를 입력해주세요.") }
            return
        }

        _uiState.update { it.copy(isLoading = true, error = null, isRegisterSuccess = false) }

        viewModelScope.launch {
            // ⭐️ 한 번만 호출하고 결과를 받음
            val result = repository.register(email, pass, role)

            if (result.isSuccess) {
                // 성공 시 로그인 화면으로 이동 트리거
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isRegisterSuccess = true,
                        navigateToLogin = true
                    )
                }
            } else {
                // 실패 시 에러 처리 (409 Conflict 등)
                val errorMsg = result.exceptionOrNull()?.message ?: "회원가입 실패"
                Log.e("AuthViewModel", "회원가입 실패: $errorMsg")
                _uiState.update { it.copy(isLoading = false, error = errorMsg) }
            }
        }
    }

    fun resetRegisterState() {
        _uiState.update { it.copy(isRegisterSuccess = false) }
    }

    fun resetError() {
        _uiState.update { it.copy(error = null) }
    }
}