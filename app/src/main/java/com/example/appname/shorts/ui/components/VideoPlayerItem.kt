package com.example.appname.shorts.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.appname.shorts.domain.model.ShortsItem

// ... (imports)

@Composable
fun VideoPlayerItem(
    shortsItem: ShortsItem,
    isSelected: Boolean,
    onLikeClicked: () -> Unit,
    onCommentIconClicked: () -> Unit // 🚨 (1) [New] 댓글 클릭 이벤트
) {
    // ... (context, exoPlayer, DisposableEffect, LaunchedEffect 코드는 동일) ...

    Box(modifier = Modifier.fillMaxSize()) {
        // ... (AndroidView 코드는 동일) ...

        Column(
            // ... (Column modifiers는 동일) ...
        ) {
            IconButton(onClick = onLikeClicked) {
                // ... (좋아요 아이콘) ...
            }
            // 🚨 (2) [Update] 댓글 IconButton에 이벤트 연결
            IconButton(onClick = onCommentIconClicked) {
                Icon(Icons.Default.ChatBubbleOutline, contentDescription = "댓글", tint = Color.White)
            }
            IconButton(onClick = { /* TODO: Shorts 공유 로직 */ }) {
                Icon(Icons.Default.Share, contentDescription = "공유", tint = Color.White)
            }
        }
    }
}