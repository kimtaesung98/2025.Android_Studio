package com.example.babful.ui.delivery.map

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.babful.data.model.DeliveryItem
import com.example.babful.util.MapType
import com.example.babful.util.MapUtils
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun UniversalMap(
    mapState: UniversalMapState,
    onNavigateToStore: (String) -> Unit
) {
    // ⭐️ 1. 국가별 지도 타입 결정
    // (테스트를 위해 강제로 GOOGLE 또는 NAVER로 변경 가능)
    val mapType = MapUtils.getMapType()

    Box(modifier = Modifier.fillMaxSize()) {
        when (mapType) {
            MapType.GOOGLE -> {
                // ⭐️ 2. 구글 지도 (API Key 이슈로 현재는 로딩 안 될 수 있음)
                GoogleMapWrapper(mapState, onNavigateToStore)
            }
            MapType.NAVER -> {
                // ⭐️ 3. 네이버 지도 (추후 구현 예정 - 현재는 Placeholder)
                PlaceholderMap("네이버 지도 (한국)")
            }
            else -> {
                PlaceholderMap("지도 서비스 준비중")
            }
        }
    }
}

// --- [구글 지도 구현체] ---
@Composable
fun GoogleMapWrapper(
    mapState: UniversalMapState,
    onNavigateToStore: (String) -> Unit
) {
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            LatLng(mapState.centerLat, mapState.centerLng),
            mapState.zoomLevel
        )
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState
    ) {
        mapState.markers.forEach { item ->
            if (item.lat != 0.0 && item.lng != 0.0) {
                Marker(
                    state = MarkerState(position = LatLng(item.lat, item.lng)),
                    title = item.storeName,
                    snippet = "클릭하여 상세 보기",
                    onInfoWindowClick = { onNavigateToStore(item.id) }
                )
            }
        }
    }
}

// --- [대체 화면 (Placeholder)] ---
@Composable
fun PlaceholderMap(message: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray),
        contentAlignment = Alignment.Center
    ) {
        Text(text = message, color = Color.Black)
    }
}