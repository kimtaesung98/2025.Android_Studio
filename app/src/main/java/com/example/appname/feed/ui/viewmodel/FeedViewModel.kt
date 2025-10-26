package com.example.appname.feed.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appname.feed.domain.model.Post
import com.example.appname.feed.domain.usecase.GetFeedPostsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

import androidx.lifecycle.viewModelScope // viewModelScope import
import com.example.appname.feed.domain.usecase.LikePostUseCase // 🚨 (1)
import com.example.appname.feed.domain.usecase.SubmitCommentUseCase // 🚨 (1)
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch // launch import
import javax.inject.Inject
/**
 * [설계 의도]
 * 2단계: ViewModel은 UseCase에 의존하며, UI 상태 관리(StateFlow)에만 집중합니다.
 * '어떻게' 데이터를 가져오는지는 UseCase와 Repository가 담당합니다.
 */

// 1. UI 상태를 정의하는 Data Class (변경 없음)
data class FeedUiState(
    val posts: List<Post> = emptyList(),
    val commentingPostId: Int? = null,
    val currentCommentText: String = ""
)

// 2. ViewModel은 이제 생성자에서 GetFeedPostsUseCase를 주입받습니다.
@HiltViewModel
class FeedViewModel @Inject constructor(
    // (2) 🚨 Hilt가 3개의 UseCase를 모두 자동으로 주입
    private val getFeedPostsUseCase: GetFeedPostsUseCase,
    private val likePostUseCase: LikePostUseCase,
    private val submitCommentUseCase: SubmitCommentUseCase
) : ViewModel() {

    // region 1. UI 상태 관리
    private val _uiState = MutableStateFlow(FeedUiState())
    val uiState = _uiState.asStateFlow()
    // endregion

    init {
        // 3. ViewModel이 생성될 때, UseCase를 통해 데이터 로드를 시작합니다.
        loadPosts()
    }

    // region 2. 데이터 로직 (UseCase 호출)

    /**
     * GetFeedPostsUseCase를 호출하여 게시물 목록을 가져옵니다.
     * Flow를 구독(collect)하여 UI 상태를 업데이트합니다.
     */
    private fun loadPosts() {
        // 4. UseCase는 Flow를 반환하므로 viewModelScope에서 수집(collect)합니다.
        getFeedPostsUseCase() // 'invoke()'는 생략 가능
            .onEach { posts -> // 5. UseCase가 성공적으로 데이터를 가져오면
                _uiState.update { it.copy(posts = posts) }
            }
            .catch { e -> // 6. 데이터 로드 중 에러 발생 시
                // TODO: 2단계 심화 - 에러 상태를 UiState에 포함시켜 UI에 표시
                println("Error loading posts: ${e.message}")
            }
            .launchIn(viewModelScope) // 7. viewModelScope에서 Flow 스트림 실행
    }

    // 🚨 1단계에 있었던 loadDummyPosts() 함수는 여기서 삭제되었습니다.
    // endregion

    // region 3. UI 이벤트 처리 (현재는 ViewModel이 직접 처리)
    // TODO: 2단계 심화 - 이 로직들도 모두 UseCase로 분리해야 합니다.

    /**
     * '좋아요' 아이콘 클릭 이벤트 처리
     */
    fun onLikeClicked(postId: Int) {
        // (3) 🚨 ViewModel이 직접 상태를 조작하던 로직 삭제
        // (4) 🚨 UseCase(suspend 함수)를 viewModelScope에서 호출
        viewModelScope.launch {
            val result = likePostUseCase(postId) // UseCase 호출

            // (임시) 1단계 뼈대에서는 Repository가 실시간 Flow가 아니므로,
            // '좋아요' 성공 시 UI를 수동으로 갱신
            if (result.isSuccess) {
                _uiState.update { currentState ->
                    val updatedPosts = currentState.posts.map {
                        if (it.id == postId) it.copy(isLiked = !it.isLiked) else it
                    }
                    currentState.copy(posts = updatedPosts)
                }
            }
            // TODO: 실패 시 UI 피드백 (예: Toast)
        }
    }

    /**
     * '댓글' 아이콘 클릭 이벤트 처리 (입력창 토글)
     */
    fun onCommentIconClicked(postId: Int) {
        _uiState.update { currentState ->
            if (currentState.commentingPostId == postId) {
                currentState.copy(commentingPostId = null, currentCommentText = "")
            } else {
                currentState.copy(commentingPostId = postId, currentCommentText = "")
            }
        }
    }

    /**
     * '댓글' 텍스트 변경 이벤트 처리
     */
    fun onCommentTextChanged(newText: String) {
        _uiState.update {
            it.copy(currentCommentText = newText)
        }
    }

    /**
     * '댓글 제출' 이벤트 처리
     */
    fun onSubmitComment(postId: Int) {
        val commentText = uiState.value.currentCommentText

        // (5) 🚨 UseCase(suspend 함수)를 viewModelScope에서 호출
        viewModelScope.launch {
            val result = submitCommentUseCase(postId, commentText) // UseCase 호출

            result.onSuccess {
                // (6) 성공 시 입력창 닫기
                _uiState.update {
                    it.copy(commentingPostId = null, currentCommentText = "")
                }
            }
            result.onFailure { exception ->
                // TODO: 실패 시 UI 피드백 (예: "댓글 내용이 비어있습니다." Toast)
                println("Comment submit failed: ${exception.message}")
            }
        }
    }
    // endregion
}