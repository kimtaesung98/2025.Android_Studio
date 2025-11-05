package com.example.appname.delivery.data.remote.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DeliveryRequestDto( // (Go 서버 POST /delivery/submit) 요청 Body
    val restaurant: String,
    val menu: String,
    val address: String
)

@JsonClass(generateAdapter = true)
data class DeliveryResponseDto( // (Go 서버 POST /delivery/submit) 응답
    val orderId: String,
    val status: String,
    val estimatedTime: Long // (예시: 30분)
)