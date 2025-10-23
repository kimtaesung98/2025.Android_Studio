package com.example.appname.feed.ui

import androidx.lifecycle.ViewModel
import com.example.appname.feed.domain.model.Post
import com.example.appname.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// (2) 🚨 FeedUiState의 정의. 이 파일 안에 있어야 합니다.
data class FeedUiState(
    val posts: List<Post> = emptyList(),
    val commentingPostId: Int? = null,
    val currentCommentText: String = ""
)

class FeedViewModel : ViewModel() {
    // ... (ViewModel의 나머지 코드는 동일) ...
    private val _uiState = MutableStateFlow(FeedUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadDummyPosts()
    }

    private fun loadDummyPosts() {
        val dummyPosts = listOf(
            Post(
                1,
                "Gemini",
                "Jetpack Compose로 피드 화면 만들기!",
                R.drawable.ic_launcher_background,
                isLiked = true
            ),
            Post(2, "Android Studio", "새로운 버전이 출시되었습니다.", R.drawable.ic_launcher_background),
            Post(
                3,
                "Kotlin",
                "코틀린 2.0이 점점 다가옵니다.",
                R.drawable.ic_launcher_background,
                isLiked = true
            ),
            Post(4, "Developer", "오늘도 즐거운 코딩! #일상", R.drawable.ic_launcher_background)
        )
        _uiState.value = FeedUiState(posts = dummyPosts)
    }

    fun onLikeClicked(postId: Int) {
        _uiState.update { currentState ->
            val updatedPosts = currentState.posts.map { post ->
                if (post.id == postId) {
                    post.copy(isLiked = !post.isLiked)
                } else {
                    post
                }
            }
            currentState.copy(posts = updatedPosts)
        }
    }

    fun onCommentIconClicked(postId: Int) {
        _uiState.update { currentState ->
            if (currentState.commentingPostId == postId) {
                currentState.copy(commentingPostId = null, currentCommentText = "")
            } else {
                currentState.copy(commentingPostId = postId, currentCommentText = "")
            }
        }
    }

    fun onCommentTextChanged(newText: String) {
        _uiState.update {
            it.copy(currentCommentText = newText)
        }
    }

    fun onSubmitComment(postId: Int) {
        val commentText = uiState.value.currentCommentText
        if (commentText.isBlank()) return
        println("Comment Submitted on Post $postId: $commentText")
        _uiState.update {
            it.copy(commentingPostId = null, currentCommentText = "")
        }
    }
}