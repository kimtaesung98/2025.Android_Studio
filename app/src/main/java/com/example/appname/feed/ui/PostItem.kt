package com.example.appname.feed.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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

@Composable
fun PostItem(
    post: Post,
    isCommenting: Boolean,
    commentText: String,
    onLikeClicked: () -> Unit,
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