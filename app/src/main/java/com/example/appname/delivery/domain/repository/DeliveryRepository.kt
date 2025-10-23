package com.example.appname.delivery.domain.repository

import com.example.appname.delivery.domain.model.DeliveryRequest

/**
 * [설계 의도 요약]
 * Delivery(배달) 데이터 처리에 대한 '규칙(Interface)'을 정의합니다.
 * UseCase는 이 인터페이스에만 의존합니다.
 */
interface DeliveryRepository {

    /**
     * 배달 요청을 서버(또는 DB)에 제출합니다.
     * @param request 배달 요청 데이터
     * @return Result<Boolean> - 요청의 성공/실패 여부를 반환
     */
    suspend fun submitRequest(request: DeliveryRequest): Result<Boolean>

    // TODO: implement details (예: fun getMyDeliveryHistory(): Flow<List<DeliveryRequest>>)
}