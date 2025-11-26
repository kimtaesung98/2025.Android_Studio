package com.example.babful.data.model

import com.google.gson.annotations.SerializedName

data class ActiveOrder(
    val id: Int,
    @SerializedName("store_name") val storeName: String,
    val status: String
)