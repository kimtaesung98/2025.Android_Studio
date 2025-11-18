package com.example.babful.ui.delivery.map

import androidx.compose.runtime.Stable
import com.example.babful.data.model.DeliveryItem

// ⭐️ 지도 제어를 위한 공통 상태 (Interface 역할)
@Stable
data class UniversalMapState(
    val centerLat: Double = 37.4979, // 강남역 (기본)
    val centerLng: Double = 127.0276,
    val zoomLevel: Float = 14f,
    val markers: List<DeliveryItem> = emptyList()
)

// ⭐️ 지도 동작 이벤트 (Interface의 함수 역할)
interface MapActions {
    fun onCameraMove(lat: Double, lng: Double)
    fun onMarkerClick(storeId: String)
}