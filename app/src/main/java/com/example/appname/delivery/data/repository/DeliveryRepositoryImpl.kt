package com.example.appname.delivery.data.repository

import com.example.appname.delivery.data.remote.api.DeliveryApi
import com.example.appname.delivery.data.remote.model.DeliveryRequestDto
import com.example.appname.delivery.domain.model.DeliveryRequest
import com.example.appname.delivery.domain.repository.DeliveryRepository
import javax.inject.Inject

/**
 * [설계 의도 요약]
 * 3단계(Retrofit): Hilt로부터 DeliveryApi(Network)를 주입받습니다.
 */
class DeliveryRepositoryImpl @Inject constructor(
    private val deliveryApi: DeliveryApi // (1) 🚨 Hilt가 Retrofit API 주입
) : DeliveryRepository {

    /**
     * (2) 🚨 [Update] 'submitRequest' 로직: API 호출로 변경
     */
    override suspend fun submitRequest(request: DeliveryRequest): Result<Boolean> {
        return try {
            // (3) Domain Model -> DTO 변환
            val requestDto = DeliveryRequestDto(
                restaurant = request.restaurant,
                menu = request.menu,
                address = request.address
            )

            val response = deliveryApi.submitDelivery(requestDto) // 👈 API 호출

            if (response.isSuccessful && response.body() != null) {
                // (4) 서버가 성공적으로 주문을 생성함
                Result.success(true)
            } else {
                Result.failure(Exception("주문 접수 실패: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e) // (예: 인터넷 없음)
        }
    }
}