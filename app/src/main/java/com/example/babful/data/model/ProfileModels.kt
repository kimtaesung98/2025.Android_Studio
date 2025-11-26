package com.example.babful.data.model

import com.google.gson.annotations.SerializedName

// 내 주문 내역 모델
data class MyOrder(
    val id: Int,
    @SerializedName("store_name") val storeName: String,
    val amount: Int,
    val status: String,
    @SerializedName("created_at") val createdAt: String
)

// ⭐️ [확인] 구독 목록 모델 (이 부분이 누락되었는지 확인해주세요)
data class MySubscription(
    @SerializedName("store_id") val storeId: String,
    @SerializedName("store_name") val storeName: String,
    @SerializedName("image_url") val imageUrl: String
)