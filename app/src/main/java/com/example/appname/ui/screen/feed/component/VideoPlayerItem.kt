package com.example.appname.ui.screen.shorts.components

// (1) 🚨 VideoPlayerItem이 사용하는 모든 부품을 import 합니다.
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.example.appname.model.ShortsItem // (2) 🚨 ShortsItem 모델 import

// (3) 🚨 ShortsScreen.kt에서 잘라내어 옮겨온 코드
@Composable
fun VideoPlayerItem(
    shortsItem: ShortsItem,
    isSelected: Boolean,
    onLikeClicked: () -> Unit
) {
    val context = LocalContext.current

    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(shortsItem.videoUrl))
            prepare()
            playWhenReady = false // LaunchedEffect에서 재생 제어
        }
    }

    // Composable이 화면에서 사라질 때 플레이어 해제
    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    // 선택된 페이지만 재생/정지
    LaunchedEffect(key1 = isSelected) {
        if (isSelected) {
            exoPlayer.play()
        } else {
            exoPlayer.pause()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // 비디오 플레이어 (배경)
        AndroidView(
            factory = {
                PlayerView(it).apply {
                    player = exoPlayer
                    useController = false // 커스텀 UI 사용
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // 아이콘 버튼 UI (전경)
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            IconButton(onClick = onLikeClicked) {
                Icon(
                    imageVector = if (shortsItem.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "좋아요",
                    tint = if (shortsItem.isLiked) Color.Red else Color.White
                )
            }
            IconButton(onClick = { /* TODO: Shorts 댓글 로직 */ }) {
                Icon(Icons.Default.ChatBubbleOutline, contentDescription = "댓글", tint = Color.White)
            }
            IconButton(onClick = { /* TODO: Shorts 공유 로직 */ }) {
                Icon(Icons.Default.Share, contentDescription = "공유", tint = Color.White)
            }
        }
    }
}