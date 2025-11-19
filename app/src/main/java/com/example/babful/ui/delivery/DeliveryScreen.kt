package com.example.babful.ui.delivery

import android.Manifest
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.babful.data.model.DeliveryItem
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState

@Composable
fun DeliveryScreen(
    viewModel: DeliveryViewModel = hiltViewModel(),
    onNavigateToStore: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // ⭐️ [신규] 위치 권한 요청 런처
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                Toast.makeText(context, "정확한 위치 권한 허용됨", Toast.LENGTH_SHORT).show()
                viewModel.startLocationUpdates() // 권한 허용되면 위치 업데이트 시작
            }
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                Toast.makeText(context, "대략적인 위치 권한 허용됨", Toast.LENGTH_SHORT).show()
                viewModel.startLocationUpdates()
            }
            else -> {
                Toast.makeText(context, "위치 권한이 거부되었습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // ⭐️ [신규] 화면 진입 시 위치 권한 요청
    LaunchedEffect(Unit) {
        locationPermissionLauncher.launch(
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
        )
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(37.4979, 127.0276), 14f) // 초기 강남역
    }

    // ⭐️ [수정] 현재 위치가 업데이트되면 카메라 이동
    val currentLocation = uiState.currentLocation
    LaunchedEffect(currentLocation) {
        currentLocation?.let { location ->
            val newLatLng = LatLng(location.latitude, location.longitude)
            // 현재 카메라 위치와 다를 때만 이동 (불필요한 이동 방지)
            if (cameraPositionState.position.target != newLatLng) {
                cameraPositionState.animate(
                    update = CameraUpdateFactory.newCameraPosition(
                        CameraPosition.fromLatLngZoom(newLatLng, 16f)
                    ),
                    durationMs = 1000
                )
            }
        }
    }


    Column(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxWidth().weight(0.4f)) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = remember { // ⭐️ [신규] 내 위치 버튼 표시
                    MapProperties(isMyLocationEnabled = uiState.currentLocation != null)
                }
            ) {
                // ⭐️ [신규] 사용자 현재 위치에 마커 표시 (선택 사항, 기본 내 위치 파란 점)
                uiState.currentLocation?.let { location ->
                    Marker(
                        state = rememberMarkerState(position = LatLng(location.latitude, location.longitude)),
                        title = "내 위치",
                        snippet = "현재 위치",
                        icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
                    )
                }

                uiState.deliveryItems.forEach { item ->
                    if (item.lat != 0.0 && item.lng != 0.0) {
                        Marker(
                            state = rememberMarkerState(position = LatLng(item.lat, item.lng)),
                            title = item.storeName,
                            snippet = "클릭하여 길찾기 (구글지도)", // 기존과 동일, 다음 단계에서 인앱 길찾기로 변경
                            onInfoWindowClick = {
                                // 기존 외부 앱 연동 (다음 단계에서 인앱 경로 표시로 대체)
                                val gmmIntentUri = "geo:${item.lat},${item.lng}?q=${item.lat},${item.lng}(${item.storeName})".toUri()
                                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                                mapIntent.setPackage("com.google.android.apps.maps")
                                try { context.startActivity(mapIntent) }
                                catch (_: Exception) { Toast.makeText(context, "구글 지도 앱 미설치", Toast.LENGTH_SHORT).show() }
                            }
                        )
                    }
                }
            }
        }

        // 하단 리스트 (기존 동일)
        Box(modifier = Modifier.fillMaxWidth().weight(0.6f)) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Text(text = "주변 배달 맛집", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                    items(uiState.deliveryItems) { item ->
                        DeliveryItemRow(
                            item = item,
                            onClick = { onNavigateToStore(item.id) }
                        )
                    }
                }
            }
        }
    }
}

// (DeliveryItemRow는 기존 코드 유지)
@Composable
fun DeliveryItemRow(item: DeliveryItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().height(100.dp).clickable { onClick() }
    ) {
        Row(modifier = Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = item.storeImageUrl ?: "https://picsum.photos/100",
                contentDescription = null,
                modifier = Modifier.size(100.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = item.storeName, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(text = "${item.estimatedTimeInMinutes}분 • ${item.status}", color = Color.Gray)
            }
        }
    }
}
