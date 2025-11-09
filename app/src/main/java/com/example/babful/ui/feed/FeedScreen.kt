package com.example.babful.ui.feed

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue // ⭐️ [신규] getValue 임포트
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle // ⭐️ [신규]
import androidx.lifecycle.viewmodel.compose.viewModel // ⭐️ [신규]
import com.example.babful.data.model.FeedItem
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
@Composable
fun FeedScreen(
    // ⭐️ [신규] viewModel() 헬퍼 함수로 ViewModel 인스턴스 주입
    viewModel: FeedViewModel = viewModel()
) {
    // ⭐️ [신규] ViewModel의 StateFlow를 구독하고, 변경될 때마다 uiState가 갱신됨
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        Log.d("FeedScreen", "피드 화면이 띄워졌습니다 (ViewModel 적용)")
    }

    // ⭐️ [제거] 5단계에 있던 'val feedItems = (1..50).map { ... }' 로직 삭제
    // (이 로직은 FeedViewModel로 이동했음)

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "밥풀 피드 (VM)", // 타이틀 변경
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(Color.Gray.copy(alpha = 0.1f))
        ) {
            // ⭐️ [수정] 5단계의 feedItems 대신 viewModel의 uiState.feedItems 사용
            items(uiState.feedItems, key = { it.id }) { item ->
                FeedItemView(item = item)
            }
        }
    }
}

// ⭐️ (FeedItemView 코드는 5단계와 '완전히 동일' - 수정 불필요)
@Composable
fun FeedItemView(item: FeedItem) {
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

        // 3. 글 내용 (이미지 아래로 이동)
        Text(
            text = item.content,
            fontSize = 15.sp,
            modifier = Modifier.padding(16.dp)
        )

        // 4. 좋아요 (글 내용 아래로 이동)
        Text(
            text = "좋아요 ${item.likesCount}개",
            fontSize = 14.sp,
            color = Color.DarkGray,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}