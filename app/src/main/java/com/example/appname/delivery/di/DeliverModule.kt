package com.example.appname.delivery.di

import com.example.appname.delivery.data.repository.DeliveryRepositoryImpl
import com.example.appname.delivery.domain.repository.DeliveryRepository
import com.example.appname.delivery.domain.usecase.SubmitDeliveryRequestUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import com.example.appname.delivery.data.remote.api.DeliveryApi
import retrofit2.Retrofit
/**
 * [설계 의도 요약]
 * Hilt가 Delivery 모듈의 의존성을 주입(Inject)하는 방법을 정의합니다.
 */
@Module
@InstallIn(SingletonComponent::class)
object DeliveryModule {

    // 🚨 [New] Retrofit을 주입받아 DeliveryApi 구현체를 생성
    @Provides
    @Singleton
    fun provideDeliveryApi(retrofit: Retrofit): DeliveryApi {
        return retrofit.create(DeliveryApi::class.java)
    }

    /**
     * DeliveryRepository(인터페이스)를 요청하면 DeliveryRepositoryImpl(구현체)을 제공합니다.
     */
    @Provides
    @Singleton
    fun provideDeliveryRepository(
        deliveryApi: DeliveryApi // 👈 Hilt가 제공
    ): DeliveryRepository {
        return DeliveryRepositoryImpl(deliveryApi)
    }

    /**
     * SubmitDeliveryRequestUseCase를 요청하면, Hilt가 Repository를 주입하여 생성해 줍니다.
     */
    @Provides
    fun provideSubmitDeliveryRequestUseCase(repository: DeliveryRepository): SubmitDeliveryRequestUseCase {
        return SubmitDeliveryRequestUseCase(repository)
    }
}