package com.example.babful.ui.delivery.map

import com.example.babful.data.model.DeliveryItem

/**
 * 지도 UI 상태를 나타내는 데이터 클래스
 *
 * @param centerLat 지도의 중심 위도
 * @param centerLng 지도의 중심 경도
 * @param zoomLevel 지도의 확대/축소 레벨
 * @param markers 지도에 표시할 마커 목록 (DeliveryItem 사용)
 * @param routePolyline 지도에 그릴 경로 정보 (Polyline encoded string)
 */
data class UniversalMapState(
    val centerLat: Double = 37.4979, // 기본값: 강남역
    val centerLng: Double = 127.0276,
    val zoomLevel: Float = 14f,
    val markers: List<DeliveryItem> = emptyList(),
    val routePolyline: String? = null // 경로 그리기 데이터
)
