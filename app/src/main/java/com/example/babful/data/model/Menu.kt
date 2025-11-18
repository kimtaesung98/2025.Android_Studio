package com.example.babful.data.model
import com.google.gson.annotations.SerializedName

data class Menu(
    val id: Int,
    @SerializedName("store_id") val storeId: Int,
    val name: String,
    val price: Int,
    @SerializedName("image_url") val imageUrl: String
)

data class CreateMenuRequest(
    @SerializedName("store_id") val storeId: Int,
    val name: String,
    val price: Int
)