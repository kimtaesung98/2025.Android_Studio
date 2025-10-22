package com.example.appname.ui.screen.shorts

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
// (1) 🚨 분리된 VideoPlayerItem을 import
import com.example.appname.ui.screen.shorts.components.VideoPlayerItem
import com.example.appname.viewmodel.ShortsViewModel

@Composable
fun ShortsScreen(shortsViewModel: ShortsViewModel = viewModel()) {
    val uiState by shortsViewModel.uiState.collectAsState()
    val pagerState = rememberPagerState(pageCount = { uiState.items.size })

    VerticalPager(
        state = pagerState,
        modifier = Modifier.fillMaxSize(),
        pageSize = PageSize.Fill
    ) { pageIndex ->
        if (uiState.items.isNotEmpty()) {
            val isSelected = (pagerState.currentPage == pageIndex)
            val currentItem = uiState.items[pageIndex]

            // (2) 🚨 이제 VideoPlayerItem은 외부에서 가져온 Composable
            VideoPlayerItem(
                shortsItem = currentItem,
                isSelected = isSelected,
                onLikeClicked = { shortsViewModel.onLikeClicked(currentItem.id) }
            )
        }
    }
}

// (3) 🚨 @Composable fun VideoPlayerItem(...) { ... } 코드는 여기서 삭제됨