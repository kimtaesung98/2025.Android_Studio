package com.example.appname.shorts.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.appname.shorts.domain.model.ShortsComment

/**
 * [설계 의도 요약]
 * Shorts 댓글 BottomSheet의 내부 UI입니다.
 */
@Composable
fun CommentSheetContent(
    comments: List<ShortsComment>,
    newCommentText: String,
    onNewCommentChanged: (String) -> Unit,
    onSubmitComment: () -> Unit,
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 300.dp, max = 500.dp) // 시트 높이 제한
            .padding(16.dp)
    ) {
        // (1) 헤더 (댓글 수, 닫기 버튼)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "댓글 (${comments.size})", style = MaterialTheme.typography.titleMedium)
            IconButton(onClick = onDismiss) {
                Icon(Icons.Default.Close, contentDescription = "닫기")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // (2) 댓글 목록
        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            items(comments) { comment ->
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    Text(text = comment.author, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    Text(text = comment.content, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // (3) 댓글 입력창
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = newCommentText,
                onValueChange = onNewCommentChanged,
                label = { Text("댓글 달기...") },
                modifier = Modifier.weight(1f),
                maxLines = 3
            )
            IconButton(onClick = onSubmitComment) {
                Icon(Icons.Default.Send, contentDescription = "댓글 보내기")
            }
        }
    }
}