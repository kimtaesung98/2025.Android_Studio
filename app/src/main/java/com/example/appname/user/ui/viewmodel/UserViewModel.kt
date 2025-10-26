package com.example.appname.user.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appname.user.domain.model.User
import com.example.appname.user.domain.usecase.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.appname.user.domain.usecase.LogoutUseCase
import com.example.appname.user.domain.usecase.CheckLoginStatusUseCase
/**
 * [설계 의도 요약]
 * 2단계: UserScreen의 UI 상태를 관리하고, UseCase와 통신합니다.
 */

// (1) 🚨 UI 상태(State) 정의
data class UserUiState(
    val emailText: String = "",
    val passwordText: String = "",
    val isLoading: Boolean = false,
    val loginUser: User? = null // 로그인 성공 시 사용자 정보
)

// (2) 🚨 ViewModel이 Hilt를 사용하도록 선언
@HiltViewModel
class UserViewModel @Inject constructor( // (3) 🚨 Hilt가 UseCase를 주입
    private val loginUseCase: LoginUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val checkLoginStatusUseCase: CheckLoginStatusUseCase
) : ViewModel() {
    init {
        // 🚨 (3) [New] ViewModel 생성 시, 즉시 로그인 상태 확인
        checkLoginStatus()
    }
    private fun checkLoginStatus() {
        viewModelScope.launch {
            // (4) DataStore에서 토큰을 가져와 프로필을 요청
            val result = checkLoginStatusUseCase()
            result.onSuccess { user ->
                // (5) 성공 시 (저장된 토큰이 유효하면) UI 상태를 '로그인됨'으로 변경
                _uiState.update { it.copy(loginUser = user) }
            }
            // (6) 실패 시 (토큰이 없거나 만료됨) - 아무것도 안 함 (로그인 전 상태 유지)
            result.onFailure {
                _uiState.update { it.copy(loginUser = null) }
            }
        }
    }
    private val _uiState = MutableStateFlow(UserUiState())
    val uiState = _uiState.asStateFlow()

    // (4) 🚨 Toast 등 일회성 이벤트를 위한 SharedFlow
    private val _eventFlow = MutableSharedFlow<String>()
    val eventFlow = _eventFlow.asSharedFlow()

    // (5) 🚨 UI 이벤트: 이메일 텍스트 변경
    fun onEmailChanged(email: String) {
        _uiState.update { it.copy(emailText = email) }
    }

    // (6) 🚨 UI 이벤트: 비밀번호 텍스트 변경
    fun onPasswordChanged(password: String) {
        _uiState.update { it.copy(passwordText = password) }
    }

    // (7) 🚨 UI 이벤트: 로그인 버튼 클릭
    fun onLoginClicked() {
        if (_uiState.value.isLoading) return // 로딩 중 중복 클릭 방지

        _uiState.update { it.copy(isLoading = true) } // 로딩 시작

        viewModelScope.launch {
            val result = loginUseCase(
                email = _uiState.value.emailText,
                password = _uiState.value.passwordText
            )

            result.onSuccess { user ->
                _uiState.update { it.copy(isLoading = false, loginUser = user) }
                _eventFlow.emit("로그인 성공! ${user.nickname}님 환영합니다.")
            }
            result.onFailure { exception ->
                _uiState.update { it.copy(isLoading = false) }
                _eventFlow.emit(exception.message ?: "로그인 실패")
            }
        }
    }
    fun onLogoutClicked() {
        viewModelScope.launch {
            val result = logoutUseCase() // UseCase 호출

            result.onSuccess {
                // 로그아웃 성공 시, UI 상태를 초기화
                _uiState.update {
                    it.copy(
                        loginUser = null,
                        emailText = "",
                        passwordText = ""
                    )
                }
                _eventFlow.emit("로그아웃 되었습니다.")
            }
            result.onFailure {
                // TODO: 로그아웃 실패 처리
                _eventFlow.emit("로그아웃 실패: ${it.message}")
            }
        }
    }
}