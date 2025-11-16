package com.example.babful.data.model

import com.google.gson.annotations.SerializedName

// (Room @Entity가 아님 - 이 정보는 캐시하지 않음)
data class StoreInfo(
    @SerializedName("id")
    val id: String,

    @SerializedName("store_name")
    val storeName: String,

    @SerializedName("banner_image")
    val bannerImage: String? = null,

    @SerializedName("is_subscribed")
    var isSubscribed: Boolean = false // ⭐️ ViewModel이 토글할 수 있도록 var
)