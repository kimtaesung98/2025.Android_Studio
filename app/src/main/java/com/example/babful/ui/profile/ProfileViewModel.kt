package com.example.babful.ui.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.babful.data.model.Transaction // ⭐️ [신규]
import com.example.babful.data.model.User // ⭐️ [신규]
import com.example.babful.data.repository.ProfileRepository // ⭐️ [수정]
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// ⭐️ [수정] 1. 프로필 화면의 UI 상태
data class ProfileUiState(
    val isLoading: Boolean = true, // ⭐️ (수정) true로 시작
    val navigateToLogin: Boolean = false,
    val user: User? = null, // ⭐️ [신규] 내 정보 (이메일, 포인트 잔액)
    val transactions: List<Transaction> = emptyList() // ⭐️ [신규] 포인트 내역
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: ProfileRepository // ⭐️ [수정] ProfileRepository 주입
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadProfileData() // ⭐️ [신규] 화면 진입 시 데이터 로드
    }

    // ⭐️ [신규] 2. 내 정보 + 포인트 내역 로드
    fun loadProfileData() {
        Log.d("ProfileViewModel", "프로필 정보 로드 시작...")
        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            try {
                // 1. 내 정보 (잔액)
                val user = repository.getProfileInfo()
                // 2. 포인트 내역
                val transactions = repository.getPointHistory()

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        user = user,
                        transactions = transactions
                    )
                }
                Log.d("ProfileViewModel", "프로필 로드 성공. 잔액: ${user.points}")
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "프로필 로드 실패", e)
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun loadProfileInfo() {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            try {
                val user = repository.getProfileInfo()
                // ⭐️ 성공 시 (기존 로직)
                _uiState.update { it.copy(isLoading = false, user = user) }
            } catch (e: Exception) {
                // ⭐️ [수정] 에러 발생 시 (특히 404)
                // 서버에 유저가 없다는 뜻이므로, 강제로 로그아웃 시키고 로그인 화면으로 보냅니다.
                logout()
                _uiState.update { it.copy(isLoading = false, error = "세션이 만료되었습니다. 다시 로그인해주세요.") }
            }
        }
    }

    // ⭐️ [수정] 3. '로그아웃' 버튼 클릭 시 호출
    fun logout() {
        Log.d("ProfileViewModel", "로그아웃 요청 수신...")
        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            repository.logout() // ⭐️ (수정) Repository 함수 호출

            Log.d("ProfileViewModel", "토큰 삭제 완료. 로그인 화면으로 이동.")
            _uiState.update { it.copy(isLoading = false, navigateToLogin = true) }
        }
    }

    // 4. (36단계와 동일) 네비게이션 이벤트가 '소비'되었음을 VM에 알림
    fun onNavigationDone() {
        _uiState.update { it.copy(navigateToLogin = false) }
    }
}