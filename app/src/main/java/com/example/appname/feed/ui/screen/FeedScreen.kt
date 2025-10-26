package com.example.appname.feed.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appname.feed.domain.model.Post
import com.example.appname.feed.ui.components.PostItem
import androidx.hilt.navigation.compose.hiltViewModel // (1) 🚨 hiltViewModel import
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.appname.feed.ui.viewmodel.FeedViewModel
@Composable
fun FeedScreen(feedViewModel: FeedViewModel = hiltViewModel()) {
    val uiState by feedViewModel.uiState.collectAsState()
    // (1) 스크롤 가능한 목록
    LazyColumn(
        modifier = Modifier.Companion.fillMaxSize(),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // (2) 'posts' 목록의 각 항목을 화면에 표시
        items(uiState.posts) { post ->
            PostItem(
                post = post,
                isCommenting = (uiState.commentingPostId == post.id),
                commentText = uiState.currentCommentText,
                // 🚨 (1) [New] 해당 포스트의 댓글 목록을 UiState에서 찾아 전달
                comments = uiState.commentsByPostId[post.id] ?: emptyList(),
                onLikeClicked = { feedViewModel.onLikeClicked(post.id) },
                onCommentIconClicked = { feedViewModel.onCommentIconClicked(post.id) },
                onCommentTextChanged = { feedViewModel.onCommentTextChanged(it) },
                onSubmitComment = { feedViewModel.onSubmitComment(post.id) }
            )
        }
    }
}

// (3) 게시물 하나를 그리는 Composable 함수
@Composable
fun PostItem(post: Post) {
    Card(
        modifier = Modifier.Companion.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            Image(
                painter = painterResource(id = post.imageRes),
                contentDescription = "Post Image",
                modifier = Modifier.Companion
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Companion.Crop
            )
            Column(modifier = Modifier.Companion.padding(16.dp)) {
                Text(
                    text = post.author,
                    fontWeight = FontWeight.Companion.Bold,
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.Companion.height(4.dp))
                Text(text = post.content, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}