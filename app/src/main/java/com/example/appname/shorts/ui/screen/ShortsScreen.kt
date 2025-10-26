package com.example.appname.shorts.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api // 🚨 (1) [New]
import androidx.compose.material3.ModalBottomSheet // 🚨 (1) [New]
import androidx.compose.material3.SheetState // 🚨 (1) [New]
import androidx.compose.material3.rememberModalBottomSheetState // 🚨 (1) [New]
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.appname.shorts.ui.components.CommentSheetContent // 🚨 (1) [New]
import com.example.appname.shorts.ui.components.VideoPlayerItem
import com.example.appname.shorts.ui.viewmodel.ShortsViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class) // 🚨 (2) [New] BottomSheet 사용을 위함
@Composable
fun ShortsScreen(
    shortsViewModel: ShortsViewModel = hiltViewModel()
) {
    val uiState by shortsViewModel.uiState.collectAsState()
    val pagerState = rememberPagerState(pageCount = { uiState.items.size })

    // (3) 🚨 BottomSheet 상태 관리
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    // (4) 🚨 ViewModel의 isCommentSheetVisible 상태가 변경되면 BottomSheet를 열거나 닫음
    LaunchedEffect(uiState.isCommentSheetVisible) {
        if (uiState.isCommentSheetVisible) {
            scope.launch { sheetState.show() }
        } else {
            scope.launch { sheetState.hide() }.invokeOnCompletion {
                if (!sheetState.isVisible) {
                    shortsViewModel.onDismissCommentSheet() // 애니메이션 끝나고 VM 상태 변경
                }
            }
        }
    }

    // (5) 🚨 Box로 Pager와 BottomSheet를 감싼다
    Box(modifier = Modifier.fillMaxSize()) {
        VerticalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            pageSize = PageSize.Fill
        ) { pageIndex ->
            if (uiState.items.isNotEmpty()) {
                val isSelected = (pagerState.currentPage == pageIndex)
                val currentItem = uiState.items[pageIndex]

                VideoPlayerItem(
                    shortsItem = currentItem,
                    isSelected = isSelected,
                    onLikeClicked = { shortsViewModel.onLikeClicked(currentItem.id) },
                    // (6) 🚨 '댓글' 아이콘 클릭 시 ViewModel 이벤트 호출
                    onCommentIconClicked = { shortsViewModel.onCommentIconClicked(currentItem.id) }
                )
            }
        }

        // (7) 🚨 BottomSheet Composable
        if (uiState.isCommentSheetVisible) {
            ModalBottomSheet(
                onDismissRequest = { shortsViewModel.onDismissCommentSheet() },
                sheetState = sheetState
            ) {
                // (8) 🚨 BottomSheet 내부에 표시될 컨텐츠
                CommentSheetContent(
                    comments = uiState.comments,
                    newCommentText = uiState.newCommentText,
                    onNewCommentChanged = { shortsViewModel.onNewCommentTextChanged(it) },
                    onSubmitComment = { shortsViewModel.onSubmitComment() },
                    onDismiss = { shortsViewModel.onDismissCommentSheet() }
                )
            }
        }
    }
}