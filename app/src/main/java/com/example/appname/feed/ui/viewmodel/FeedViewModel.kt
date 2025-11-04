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
import com.example.appname.feed.domain.usecase.RefreshPostsUseCase
import com.example.appname.feed.domain.usecase.LikePostUseCase // 🚨 (1)
import com.example.appname.feed.domain.usecase.SubmitCommentUseCase // 🚨 (1)
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch // launch import
import javax.inject.Inject
import com.example.appname.feed.domain.model.Comment
import com.example.appname.feed.domain.usecase.GetCommentsUseCase


/**
 * [설계 의도]
 * 2단계: ViewModel은 UseCase에 의존하며, UI 상태 관리(StateFlow)에만 집중합니다.
 * '어떻게' 데이터를 가져오는지는 UseCase와 Repository가 담당합니다.
 */

// 1. UI 상태를 정의하는 Data Class (변경 없음)
data class FeedUiState(
    val posts: List<Post> = emptyList(),
    val commentingPostId: Int? = null,
    val currentCommentText: String = "",
    val commentsByPostId: Map<Int, List<Comment>> = emptyMap() // <PostID, CommentList>
)

// 2. ViewModel은 이제 생성자에서 GetFeedPostsUseCase를 주입받습니다.
@HiltViewModel
class FeedViewModel @Inject constructor(
    private val getFeedPostsUseCase: GetFeedPostsUseCase,
    private val likePostUseCase: LikePostUseCase,
    private val submitCommentUseCase: SubmitCommentUseCase,
    private val getCommentsUseCase: GetCommentsUseCase,
    private val refreshPostsUseCase: RefreshPostsUseCase // 🚨 (2) [New] Hilt 주입
) : ViewModel() {

    // region 1. UI 상태 관리
    private val _uiState = MutableStateFlow(FeedUiState())
    val uiState = _uiState.asStateFlow()
    // endregion

    init {
        // 🚨 (3) [Update] loadPosts() 함수가 이제 2가지 일을 함
        loadPosts()
    }

    // region 2. 데이터 로직 (UseCase 호출)

    /**
     * GetFeedPostsUseCase를 호출하여 게시물 목록을 가져옵니다.
     * Flow를 구독(collect)하여 UI 상태를 업데이트합니다.
     */
    private fun loadPosts() {
        // (4) 🚨 [SSOT 1] Room DB를 즉시 구독 시작 (UI가 캐시된 데이터를 즉시 봄)
        getFeedPostsUseCase() // (GetFeedPostsUseCase는 Room Flow를 반환)
            .onEach { posts ->
                _uiState.update { it.copy(posts = posts) }
            }
            .catch { /* ... Room 읽기 오류 처리 ... */ }
            .launchIn(viewModelScope)

        // (5) 🚨 [SSOT 2] 네트워크 갱신 '요청' (백그라운드 실행)
        viewModelScope.launch {
            refreshPostsUseCase() // (Retrofit -> Room 갱신)
            // (갱신이 완료되면 4번의 Flow가 자동으로 새 데이터를 감지함)
        }
    }
    private fun loadComments(postId: Int) {
        getCommentsUseCase(postId)
            .onEach { comments ->
                _uiState.update { currentState ->
                    // 현재 댓글 맵을 복사하고, 새 댓글 목록을 덮어씀
                    val newCommentsMap = currentState.commentsByPostId.toMutableMap()
                    newCommentsMap[postId] = comments
                    currentState.copy(commentsByPostId = newCommentsMap)
                }
            }
            .catch { e ->
                // TODO: 댓글 로드 실패 시 에러 처리
                println("Error loading comments: ${e.message}")
            }
            .launchIn(viewModelScope)
    }

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
    // 🚨 (5) [Update] '댓글' 아이콘 클릭 시 댓글 로드도 함께 수행
    fun onCommentIconClicked(postId: Int) {
        _uiState.update { currentState ->
            val isAlreadyCommenting = (currentState.commentingPostId == postId)
            if (isAlreadyCommenting) {
                // 댓글 창 닫기
                currentState.copy(commentingPostId = null, currentCommentText = "")
            } else {
                // 댓글 창 열기
                loadComments(postId) // 👈 [New] 댓글 로드 시작
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
    // 🚨 (6) [Update] '댓글 제출' 성공 시 댓글 목록 새로고침
    fun onSubmitComment(postId: Int) {
        val commentText = uiState.value.currentCommentText

        viewModelScope.launch {
            val result = submitCommentUseCase(postId, commentText)

            result.onSuccess {
                // 댓글 제출 성공 시
                _uiState.update {
                    it.copy(commentingPostId = null, currentCommentText = "")
                }
                loadComments(postId) // 👈 [New] 댓글 목록 새로고침
            }
            result.onFailure { exception ->
                // ... (실패 처리) ...
                println("Comment submit failed: ${exception.message}")
            }
        }
    }
}