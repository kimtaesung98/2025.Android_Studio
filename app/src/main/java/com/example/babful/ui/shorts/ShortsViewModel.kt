package com.example.babful.ui.shorts

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.babful.data.model.ShortsItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

// 1. ViewModel이 UI에 전달할 화면 상태 (State)
data class ShortsUiState(
    val shortsItems: List<ShortsItem> = emptyList(),
    val isLoading: Boolean = false
)

// 2. ViewModel 클래스 정의
class ShortsViewModel : ViewModel() {

    // 3. UI 상태를 관리하는 StateFlow
    private val _uiState = MutableStateFlow(ShortsUiState())
    val uiState: StateFlow<ShortsUiState> = _uiState.asStateFlow()

    // 4. ViewModel이 생성(초기화)될 때 데이터 로드
    init {
        Log.d("ShortsViewModel", "ViewModel이 생성되었습니다.")
        loadShorts()
    }

    // 5. 데이터 로딩 (8단계의 가짜 데이터 생성 로직이 여기로 이동)
    private fun loadShorts() {
        val fakeShortsItems = (1..20).map { i ->
            ShortsItem(
                id = UUID.randomUUID().toString(),
                storeName = "VM-쇼츠 가게 #$i", // ViewModel에서 왔음을 구분
                storeId = "store_$i"
            )
        }

        // 6. StateFlow에 최신 데이터를 업데이트
        _uiState.update { currentState ->
            currentState.copy(
                shortsItems = fakeShortsItems
            )
        }
    }
}