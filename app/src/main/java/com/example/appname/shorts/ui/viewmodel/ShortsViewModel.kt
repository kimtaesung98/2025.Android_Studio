package com.example.appname.shorts.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
// 🚨 (1) UseCase와 Model을 import
import com.example.appname.shorts.domain.model.ShortsItem
import com.example.appname.shorts.domain.usecase.GetShortsUseCase
import com.example.appname.shorts.domain.usecase.LikeShortsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import dagger.hilt.android.lifecycle.HiltViewModel // 🚨 (1)
import javax.inject.Inject // 🚨 (1)
import com.example.appname.shorts.domain.usecase.SubmitShortsCommentUseCase
import com.example.appname.shorts.domain.model.ShortsComment // 🚨 (1) [New]
import com.example.appname.shorts.domain.usecase.GetShortsCommentsUseCase
data class ShortsUiState(
    val items: List<ShortsItem> = emptyList(),
    val isCommentSheetVisible: Boolean = false, // BottomSheet 표시 여부
    val selectedShortsId: Int? = null, // 현재 댓글을 보려는 쇼츠 ID
    val comments: List<ShortsComment> = emptyList(), // 로드된 댓글 목록
    val newCommentText: String = "" // 새 댓글 입력 텍스트
)
@HiltViewModel
class ShortsViewModel @Inject constructor(
    private val getShortsUseCase: GetShortsUseCase,
    private val likeShortsUseCase: LikeShortsUseCase,
    private val getShortsCommentsUseCase: GetShortsCommentsUseCase, // 🚨 (3) [New]
    private val submitShortsCommentUseCase: SubmitShortsCommentUseCase // 🚨 (3) [New]
) : ViewModel(){

    private val _uiState = MutableStateFlow(ShortsUiState())
    val uiState = _uiState.asStateFlow()

    init {
        // (3) ViewModel 생성 시 '목록 가져오기' UseCase 호출
        loadShorts()
    }

    /**
     * GetShortsUseCase를 호출하여 쇼츠 목록을 가져옵니다.
     */
    private fun loadShorts() {
        getShortsUseCase() // 👈 (4) '목록' UseCase 호출
            .onEach { items ->
                _uiState.update { it.copy(items = items) }
            }
            .catch { e ->
                // TODO: 에러 처리
                println("Error loading shorts: ${e.message}")
            }
            .launchIn(viewModelScope)
    }
    // 🚨 (4) [New] '댓글' 아이콘 클릭 이벤트
    fun onCommentIconClicked(shortsId: Int) {
        _uiState.update { it.copy(isCommentSheetVisible = true, selectedShortsId = shortsId) }
        loadComments(shortsId)
    }

    // 🚨 (5) [New] BottomSheet 닫기 이벤트
    fun onDismissCommentSheet() {
        _uiState.update { it.copy(isCommentSheetVisible = false, comments = emptyList(), selectedShortsId = null) }
    }

    // 🚨 (6) [New] 새 댓글 텍스트 변경 이벤트
    fun onNewCommentTextChanged(text: String) {
        _uiState.update { it.copy(newCommentText = text) }
    }

    // 🚨 (7) [New] 댓글 로드 로직
    private fun loadComments(shortsId: Int) {
        getShortsCommentsUseCase(shortsId)
            .onEach { comments ->
                _uiState.update { it.copy(comments = comments) }
            }
            .catch { /* TODO: 에러 처리 */ }
            .launchIn(viewModelScope)
    }

    // 🚨 (8) [New] 댓글 제출 이벤트
    fun onSubmitComment() {
        val shortsId = _uiState.value.selectedShortsId ?: return
        val commentText = _uiState.value.newCommentText

        viewModelScope.launch {
            val result = submitShortsCommentUseCase(shortsId, commentText)
            if (result.isSuccess) {
                _uiState.update { it.copy(newCommentText = "") } // 입력창 비우기
                loadComments(shortsId) // 댓글 목록 새로고침
            }
            // TODO: 실패 시 Toast 등 피드백
        }
    }
    // 🚨 1단계에 있던 loadDummyShorts() 함수는 삭제됨.

    /**
     * '좋아요' 클릭 이벤트 처리
     */
    fun onLikeClicked(itemId: Int) {
        // 🚨 (5) '좋아요' 로직을 ViewModel이 직접 처리하지 않음

        // 🚨 (6) '좋아요' UseCase(suspend 함수)를 viewModelScope에서 호출
        viewModelScope.launch {
            val result = likeShortsUseCase(itemId) // 👈 (7) '좋아요' UseCase 호출

            result.onFailure {
                // TODO: '좋아요' 실패 시 에러 처리
                println("Like failed: ${it.message}")
            }
            // (8) 성공 시: RepositoryImpl이 데이터를 수정했고,
            // loadShorts()의 Flow가 자동으로 새 데이터를 감지하여
            // UI를 갱신하므로 여기서는 별도 처리가 필요 없음 (단, 실시간 DB 사용 시)

            // (임시) 만약 Flow가 실시간이 아니라면, 여기서 loadShorts()를 재호출하거나
            // RepositoryImpl이 수정한 데이터를 기반으로 수동 업데이트 필요.
            // 1단계 뼈대의 RepositoryImpl은 Flow가 실시간이 아니므로,
            // '좋아요' 후 목록을 다시 불러와야 함.
            // loadShorts() -> flowOf()가 매번 새 리스트를 방출하진 않음.
            // *수정*: RepositoryImpl의 dummyItems를 수정했으므로
            // getShortsItems()가 새 Flow를 반환하도록 수정해야 함.

            // *더 나은 1단계 뼈대 수정 (ShortsRepositoryImpl.kt)*
            // private val _dummyItems = MutableStateFlow(...)
            // override fun getShortsItems(): Flow<List<ShortsItem>> = _dummyItems
            // override suspend fun toggleLikeState(...) { _dummyItems.update { ... } }
            // (위와 같이 RepositoryImpl을 수정했다면, ViewModel은 별도 처리가 필요 없음)

            // (현재 1단계 뼈대 기준 임시 해결책): '좋아요' 후 UI 즉시 반영
            if (result.isSuccess) {
                _uiState.update { currentState ->
                    val updatedItems = currentState.items.map {
                        if (it.id == itemId) it.copy(isLiked = !it.isLiked) else it
                    }
                    currentState.copy(items = updatedItems)
                }
            }
        }
    }
}