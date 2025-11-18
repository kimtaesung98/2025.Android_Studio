package com.example.babful.util

import java.util.Locale

enum class MapType {
    GOOGLE,
    NAVER, // or KAKAO
    UNKNOWN
}

object MapUtils {
    // ⭐️ 현재 지역에 따라 지도 타입 결정
    fun getMapType(): MapType {
        val country = Locale.getDefault().country.uppercase()

        return when (country) {
            "KR" -> MapType.NAVER // 한국 -> 네이버/카카오
            "JP", "US", "GB", "FR", "DE", "IT", "ES" -> MapType.GOOGLE // 주요 국가 -> 구글
            else -> MapType.GOOGLE // 기본값 -> 구글
        }
    }

    // (테스트용) 강제로 한국으로 설정하려면 이 함수 사용
    fun isKorea(): Boolean = true
}