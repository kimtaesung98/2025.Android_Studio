package com.example.babful.ui.feed

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row // ⭐️ [신규]
import androidx.compose.foundation.layout.Spacer // ⭐️ [신규]
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons // ⭐️ [신규]
import androidx.compose.material.icons.filled.Refresh // ⭐️ [신규]
import androidx.compose.material3.CircularProgressIndicator // ⭐️ [신규]
import androidx.compose.material3.Icon // ⭐️ [신규]
import androidx.compose.material3.IconButton // ⭐️ [신규]
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
// ⭐️ [제거] import androidx.compose.runtime.LaunchedEffect (2개 다 제거)
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.babful.data.model.FeedItem
import androidx.compose.foundation.layout.Arrangement // ⭐️ [신규]
import androidx.compose.foundation.lazy.LazyListState // ⭐️ [신규]
import androidx.compose.foundation.lazy.rememberLazyListState // ⭐️ [신규]
import androidx.compose.runtime.LaunchedEffect // ⭐️ [신규]
import androidx.compose.runtime.derivedStateOf // ⭐️ [신규]
import androidx.compose.runtime.remember // ⭐️ [신규]
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.material.icons.filled.Favorite // ⭐️ [신규] (채워진 하트)
import androidx.compose.material.icons.filled.FavoriteBorder // ⭐️ [신규] (빈 하트)

@Composable
fun FeedScreen(
    viewModel: FeedViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // ⭐️ [수정] VM의 isLoading 상태만 가져옴
    val isLoading = uiState.isLoading
    val isLoadingMore = uiState.isLoadingMore // ⭐️ '더보기' 로딩 상

    // ⭐️ [수정 불필요] 1. 스크롤 상태 감지 (로직 동일)
    val listState = rememberLazyListState()
    val reachedEnd by remember {
        derivedStateOf {
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()
            lastVisibleItem != null && lastVisibleItem.index >= uiState.feedItems.size - 5
        }
    }

    // ⭐️ [수정 불필요] 2. VM 호출 (로직 동일)
    LaunchedEffect(reachedEnd) {
        if (reachedEnd && !isLoading && !isLoadingMore) {
            Log.d("FeedScreen", "스크롤 끝 도달 -> viewModel.loadMoreFeed() 호출 (UI는 반경인지 모름)")
            viewModel.loadMoreFeed()
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {

        // ⭐️ [신규] 4. 타이틀과 새로고침 버튼을 위한 Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "밥풀 피드 (SWR)", // ⭐️ [수정] 타이틀 변경
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.weight(1f)) // ⭐️ 아이콘을 오른쪽으로 밀어냄

            // ⭐️ [신규] 5. 상태 기반 UI 분기 (스피너 또는 아이콘)
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp), // 아이콘과 크기 맞춤
                    strokeWidth = 2.dp
                )
            } else {
                IconButton(onClick = {
                    Log.d("FeedScreen", "새로고침 아이콘 클릭 -> viewModel.refreshFeed() 호출")
                    viewModel.refreshFeed() // ⭐️ VM 함수 호출
                }) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "새로고침"
                    )
                }
            }
        }

        // ⭐️ [제거] 6. Box 래퍼 및 nestedScroll (제거)
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(Color.Gray.copy(alpha = 0.1f)),
            state = listState // ⭐️ [수정] LazyListState 연결
        ) {
            items(uiState.feedItems, key = { it.id }) { item ->
                FeedItemView(
                    item = item,
                    // ⭐️ [신규] 클릭 이벤트를 ViewModel까지 전달
                    onLikeClicked = {
                        Log.d("FeedScreen", "Like 클릭됨: ${item.id}")
                        viewModel.likeFeedItem(item.id)
                    }
                )
            }

            // ⭐️ [신규] 4. '더보기' 로딩 UI (목록 맨 아래에 추가)
            if (isLoadingMore) {
                item { // LazyColumn의 마지막 아이템으로 추가
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(32.dp))
                    }
                }
            }
        }
    }
}
@Composable
fun FeedItemView(
    item: FeedItem,
    onLikeClicked: () -> Unit // ⭐️ [신규] 클릭 이벤트 람다
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp) // ⭐️ 좌우 패딩 제거 (이미지가 꽉 차도록)
            .background(Color.White)
    ) {
        // 1. 사용자 정보 (프로필 + 이름)
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // ⭐️ [수정] Box -> AsyncImage (프로필 이미지)
            AsyncImage(
                model = item.userProfileImageUrl, // ⭐️ VM에서 받은 URL
                contentDescription = "${item.userName} 프로필 이미지",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape) // ⭐️ 원형으로 자르기
                    .background(Color.LightGray), // ⭐️ 로딩 중 배경색
                contentScale = ContentScale.Crop // ⭐️ 이미지 비율 (Crop)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = item.userName,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // ⭐️ [신규] 2. 피드 이미지
        AsyncImage(
            model = item.postImageUrl, // ⭐️ VM에서 받은 URL
            contentDescription = "피드 이미지",
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f) // ⭐️ 1:1 비율
                .background(Color.LightGray), // ⭐️ 로딩 중 배경색
            contentScale = ContentScale.Crop
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp), // 아이콘 버튼 패딩 고려
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onLikeClicked) {
                Icon(
                    // ⭐️ [핵심] isLiked가 true면 '채워진 하트', false면 '빈 하트'
                    imageVector = if (item.isLiked) {
                        Icons.Default.Favorite
                    } else {
                        Icons.Default.FavoriteBorder
                    },
                    contentDescription = "좋아요",
                    // ⭐️ [핵심] isLiked가 true면 '빨간색', false면 '회색'
                    tint = if (item.isLiked) Color.Red else Color.Gray
                )
            }
            // (다른 아이콘들... 예: 댓글)
            // IconButton(onClick = { /* TODO */ }) { ... }
        }

        // 4. 좋아요 카운트 (Row 아이콘 아래로 이동)
        Text(
            // ⭐️ (33단계 Int? 수정 반영)
            text = "좋아요 ${item.likesCount ?: 0}개",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold, // ⭐️ 글씨 굵게
            color = Color.Black, // ⭐️ 색상 변경
            modifier = Modifier.padding(horizontal = 16.dp) // ⭐️ 패딩 조정
        )

        // 5. 글 내용 (좋아요 카운트 아래로 이동)
        Text(
            text = item.content,
            fontSize = 15.sp,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp) // ⭐️ 패딩 조정
        )
    }
}