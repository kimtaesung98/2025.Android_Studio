package com.example.deliveryapp2.data.network.api

import com.example.deliveryapp2.data.model.Store
import retrofit2.http.GET
import retrofit2.http.Path

interface StoreApi {
    @GET("/stores")
    suspend fun getStores(): List<Store>

    @GET("/stores/{id}")
    suspend fun getStoreDetail(@Path("id") id: String): Store
}