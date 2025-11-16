package com.example.babful.ui.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.babful.data.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// 1. 프로필 화면의 UI 상태
data class ProfileUiState(
    val isLoading: Boolean = false,
    val navigateToLogin: Boolean = false // ⭐️ 로그아웃 완료 시 네비게이션 트리거
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val prefsRepo: UserPreferencesRepository // ⭐️ 31단계의 DataStore 래퍼
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState = _uiState.asStateFlow()

    // 2. '로그아웃' 버튼 클릭 시 호출
    fun logout() {
        Log.d("ProfileViewModel", "로그아웃 요청 수신...")
        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            // 1. DataStore에서 JWT 토큰 삭제
            prefsRepo.clearJwtToken()

            Log.d("ProfileViewModel", "토큰 삭제 완료. 로그인 화면으로 이동.")

            // 2. UI에 '네비게이션' 이벤트 전달
            _uiState.update { it.copy(isLoading = false, navigateToLogin = true) }
        }
    }

    // 3. (31단계와 동일) 네비게이션 이벤트가 '소비'되었음을 VM에 알림
    fun onNavigationDone() {
        _uiState.update { it.copy(navigateToLogin = false) }
    }
}