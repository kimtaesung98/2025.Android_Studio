package com.example.appname.feed.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.appname.feed.domain.model.Post
import com.example.appname.feed.domain.model.Comment
@Composable
fun PostItem(
    post: Post,
    isCommenting: Boolean,
    commentText: String,
    onLikeClicked: () -> Unit,
    comments: List<Comment>,
    onCommentIconClicked: () -> Unit,
    onCommentTextChanged: (String) -> Unit,
    onSubmitComment: () -> Unit
) {
    Card(
        modifier = Modifier.Companion.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            Image(
                painter = painterResource(id = post.imageRes), // 👈 R 클래스 사용
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

            // ... (Row 및 하단 코드는 동일) ...
            Row(
                modifier = Modifier.Companion
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.Companion.CenterVertically
            ) {
                IconButton(onClick = onLikeClicked) {
                    Icon(
                        imageVector = if (post.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "좋아요",
                        tint = if (post.isLiked) Color.Companion.Red else MaterialTheme.colorScheme.onSurface
                    )
                }
                IconButton(onClick = onCommentIconClicked) {
                    Icon(
                        imageVector = Icons.Default.ChatBubbleOutline,
                        contentDescription = "댓글"
                    )
                }
                IconButton(onClick = { /* TODO: 공유 로직 */ }) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "공유"
                    )
                }
            }

            if (isCommenting) {
                Row(
                    modifier = Modifier.Companion.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.Companion.CenterVertically
                ) {
                    OutlinedTextField(
                        value = commentText,
                        onValueChange = onCommentTextChanged,
                        label = { Text("댓글 달기...") },
                        modifier = Modifier.Companion.weight(1f),
                        maxLines = 3
                    )
                    IconButton(onClick = onSubmitComment) {
                        Icon(Icons.Default.Send, contentDescription = "댓글 보내기")
                    }
                }
            }
        }
    }
}
// 🚨 (5) [New] 댓글 목록을 그리는 별도 Composable (PostItem.kt 파일 하단에 추가)
@Composable
fun CommentList(comments: List<Comment>) {
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth()
            .heightIn(max = 150.dp) // 댓글 목록이 너무 길어지는 것을 방지
    ) {
        if (comments.isEmpty()) {
            Text(
                text = "아직 댓글이 없습니다.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            // (참고: LazyColumn 안에 LazyColumn은 성능 이슈가 있을 수 있으나,
            // heightIn(max)로 높이를 제한하면 Column/forEach로 대체 가능)
            comments.forEach { comment ->
                CommentItem(comment = comment)
            }
        }
    }
}

// 🚨 (6) [New] 댓글 하나를 그리는 Composable (PostItem.kt 파일 하단에 추가)
@Composable
fun CommentItem(comment: Comment) {
    Row(
        modifier = Modifier.padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = comment.author,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = comment.content,
            style = MaterialTheme.typography.bodySmall
        )
    }
}