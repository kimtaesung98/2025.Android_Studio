package com.project.appname.model

// (1) 서버나 데이터베이스로 전달될 순수한 데이터 구조를 정의
data class DeliveryRequest(
    val restaurant: String,
    val menu: String,
    val address: String,
    val requestTime: Long = System.currentTimeMillis() // (2) 요청 시각과 같은 추가 정보도 포함 가능
)