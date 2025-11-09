package com.example.babful.ui.feed

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.babful.data.model.FeedItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

// 1. ViewModel이 UI에 전달할 화면 상태 (State)
data class FeedUiState(
    val feedItems: List<FeedItem> = emptyList(), // 피드 아이템 목록
    val isLoading: Boolean = false
)

// 2. ViewModel 클래스 정의 (androidx.lifecycle.ViewModel 상속)
class FeedViewModel : ViewModel() {

    // 3. UI 상태를 관리하는 StateFlow
    // (private MutableStateFlow: 이 클래스 내부에서만 수정 가능)
    private val _uiState = MutableStateFlow(FeedUiState())
    // (public StateFlow: 외부(UI)에서는 읽기만 가능)
    val uiState: StateFlow<FeedUiState> = _uiState.asStateFlow()

    // 4. ViewModel이 생성(초기화)될 때 데이터 로드
    init {
        Log.d("FeedViewModel", "ViewModel이 생성되었습니다.")
        loadFeed()
    }

    // 5. 데이터 로딩 (5단계의 가짜 데이터 생성 로직이 여기로 이동)
    private fun loadFeed() {
        val fakeFeedItems = (1..50).map { i ->
            // ⭐️ 이미지 URL을 생성 (seed를 i로 주어 매번 같은 이미지가 나오도록)
            val imageUrl = "https://picsum.photos/seed/$i/300/300"

            FeedItem(
                id = UUID.randomUUID().toString(),
                userName = "vm_user_$i",
                // ⭐️ [수정] userProfileImageUrl, postImageUrl에 URL 할당
                userProfileImageUrl = "https://picsum.photos/seed/user_$i/100/100",
                postImageUrl = imageUrl,
                content = "이것은 $i 번째 피드 아이템입니다. (ViewModel로부터)",
                likesCount = (0..100).random()
            )
        }
        _uiState.update { it.copy(feedItems = fakeFeedItems) }
    }
}