package com.example.appname.delivery.domain.usecase

import com.example.appname.delivery.domain.model.DeliveryRequest
import com.example.appname.delivery.domain.repository.DeliveryRepository

/**
 * [설계 의도 요약]
 * "배달 요청을 제출한다"는 단일 비즈니스 로직(UseCase)을 캡슐화합니다.
 * ViewModel은 이 클래스를 주입받아 이 로직을 실행합니다.
 */
class SubmitDeliveryRequestUseCase(
    private val repository: DeliveryRepository
) {
    /**
     * UseCase를 함수처럼 호출할 수 있게 해주는 invoke 연산자
     */
    suspend operator fun invoke(request: DeliveryRequest): Result<Boolean> {
        // TODO: implement details
        // 2단계 '살 붙이기'에서 유효성 검사(validation) 등
        // '비즈니스 로직'을 여기에 추가할 수 있습니다.
        if (request.restaurant.isBlank() || request.menu.isBlank()) {
            return Result.failure(IllegalArgumentException("필수 항목이 비어있습니다."))
        }

        return repository.submitRequest(request)
    }
}