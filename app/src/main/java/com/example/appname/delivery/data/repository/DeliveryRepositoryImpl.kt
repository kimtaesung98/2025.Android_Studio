package com.example.appname.delivery.data.repository

import com.example.appname.delivery.domain.model.DeliveryRequest
import com.example.appname.delivery.domain.repository.DeliveryRepository

/**
 * [설계 의도 요약]
 * DeliveryRepository 인터페이스의 실제 구현체입니다.
 * 2단계 '살 붙이기' 단계에서 여기에 Retrofit API 호출 로직이 추가됩니다.
 */
class DeliveryRepositoryImpl : DeliveryRepository {

    /**
     * 배달 요청 제출 로직의 실제 구현
     */
    override suspend fun submitRequest(request: DeliveryRequest): Result<Boolean> {
        // TODO: implement details
        // 2단계 '살 붙이기' 에서는 이 부분에
        // Retrofit API 호출 코드를 작성합니다.

        // (임시) 1단계에서는 무조건 성공했다고 가정
        println("Repository: ${request} 요청 수신. (네트워크 요청 시뮬레이션)")
        return Result.success(true)
    }
}