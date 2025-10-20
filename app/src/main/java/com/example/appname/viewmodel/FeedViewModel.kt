package com.project.appname.viewmodel

import androidx.lifecycle.ViewModel
import com.project.appname.R
import com.project.appname.model.Post
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

data class FeedUiState(
    val posts: List<Post> = emptyList()
)

class FeedViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(FeedUiState())
    val uiState = _uiState.asStateFlow()

    init {
        // (1) 앱 실행 시 보여줄 임시(더미) 데이터 생성
        loadDummyPosts()
    }

    private fun loadDummyPosts() {
        val dummyPosts = listOf(
            Post(1, "Gemini", "Jetpack Compose로 피드 화면 만들기! 정말 간단해요.", R.drawable.ic_launcher_background),
            Post(2, "Android Studio", "새로운 버전이 출시되었습니다. 확인해보세요.", R.drawable.ic_launcher_background),
            Post(3, "Kotlin", "코틀린 2.0이 점점 다가옵니다.", R.drawable.ic_launcher_background),
            Post(4, "Developer", "오늘도 즐거운 코딩! #일상", R.drawable.ic_launcher_background)
        )
        _uiState.value = FeedUiState(posts = dummyPosts)
    }
}