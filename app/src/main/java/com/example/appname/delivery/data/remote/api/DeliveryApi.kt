package com.example.appname.delivery.data.remote.api

import com.example.appname.delivery.data.remote.model.DeliveryRequestDto
import com.example.appname.delivery.data.remote.model.DeliveryResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface DeliveryApi {
    @POST("delivery/submit")
    suspend fun submitDelivery(@Body request: DeliveryRequestDto): Response<DeliveryResponseDto>
}