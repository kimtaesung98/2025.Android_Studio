package com.example.babful.util

import java.util.Locale

// 1. 지도 타입 정의 (Enum)
enum class MapType {
    GOOGLE,
    NAVER, // 추후 확장용
    UNKNOWN
}

// 2. 지도 유틸리티 객체
object MapUtils {

    /**
     * 현재 기기의 지역 설정(Locale)을 기반으로 사용할 지도 타입을 결정합니다.
     * 한국(KR)일 경우 NAVER(또는 KAKAO)를, 그 외에는 GOOGLE을 반환합니다.
     */
    fun getMapType(): MapType {
        // 현재 기기의 국가 코드 (예: "KR", "US", "JP")
        val country = Locale.getDefault().country.uppercase()

        return when (country) {
            "KR" -> MapType.NAVER
            else -> MapType.GOOGLE
        }
    }

    /**
     * (테스트용) 현재 개발 단계에서는 네이버 지도가 구현되지 않았으므로,
     * 강제로 구글 지도를 사용하도록 하는 헬퍼 함수입니다.
     * 47단계 UI 코드에서 이 함수를 사용하면 안전합니다.
     */
    fun useGoogleMapOnly(): Boolean {
        return true
    }
}