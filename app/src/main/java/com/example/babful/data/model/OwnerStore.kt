package com.example.babful.data.model
import com.google.gson.annotations.SerializedName

data class OwnerStore(
    val id: Int,
    val name: String,
    val description: String,
    val lat: Double,
    val lng: Double,
    @SerializedName("image_url") val imageUrl: String
)

data class CreateStoreRequest(
    val name: String,
    val description: String,
    val lat: Double,
    val lng: Double
)