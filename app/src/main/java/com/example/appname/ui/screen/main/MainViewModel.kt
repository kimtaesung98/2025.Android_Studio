package com.example.appname.ui.screen.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appname.user.domain.usecase.CheckLoginStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

// (1) 네비게이션 상태 정의 (로딩, 로그인됨, 로그아웃됨)
sealed class NavigationState {
    object Loading : NavigationState()
    object LoggedIn : NavigationState()
    object LoggedOut : NavigationState()
}

/**
 * [설계 의도 요약]
 * 앱의 메인 진입점에서, 사용자의 로그인 상태를 확인하여
 * 적절한 네비게이션 그래프(로그인/메인)로 안내합니다.
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    private val checkLoginStatusUseCase: CheckLoginStatusUseCase
) : ViewModel() {

    private val _navState = MutableStateFlow<NavigationState>(NavigationState.Loading)
    val navState = _navState.asStateFlow()

    init {
        checkLoginStatus()
    }

    private fun checkLoginStatus() {
        // (2) UseCase(suspend) 대신, Repository의 'Flow'를 직접 관찰하는 것이
        // 로그아웃 시 실시간 반응에 더 좋습니다.
        // (수정) 1단계 뼈대 생성 시 CheckLoginStatusUseCase는 suspend였음.
        // (수정) UserViewModel의 init{}과 동일한 로직을 사용.
        viewModelScope.launch {
            val result = checkLoginStatusUseCase()
            if (result.isSuccess) {
                _navState.value = NavigationState.LoggedIn
            } else {
                _navState.value = NavigationState.LoggedOut
            }
        }
    }
}