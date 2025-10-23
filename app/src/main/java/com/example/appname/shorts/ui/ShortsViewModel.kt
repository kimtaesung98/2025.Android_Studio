package com.example.appname.shorts.ui

import androidx.lifecycle.ViewModel
import com.example.appname.shorts.domain.model.ShortsItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class ShortsUiState(
    val items: List<ShortsItem> = emptyList()
)

class ShortsViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ShortsUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadDummyShorts()
    }

    private fun loadDummyShorts() {
        val dummyItems = listOf(
            ShortsItem(
                1,
                "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
                "짧은 영상 1",
                isLiked = true
            ), // isLiked 상태 추가
            ShortsItem(
                2,
                "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4",
                "짧은 영상 2"
            ),
            ShortsItem(
                3,
                "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
                "짧은 영상 3",
                isLiked = false
            )
        )
        _uiState.value = ShortsUiState(items = dummyItems)
    }

    // (2) 🚨 '좋아요' 클릭 이벤트를 처리할 함수
    fun onLikeClicked(itemId: Int) {
        _uiState.update { currentState ->
            val updatedItems = currentState.items.map { item ->
                if (item.id == itemId) {
                    item.copy(isLiked = !item.isLiked)
                } else {
                    item
                }
            }
            currentState.copy(items = updatedItems)
        }
    }
}