package com.example.babful.data.repository
import com.example.babful.data.model.CreateStoreRequest
import com.example.babful.data.model.OwnerStore
import com.example.babful.data.network.ApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OwnerRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getMyStore(): OwnerStore = apiService.getMyStore()

    suspend fun createStore(name: String, desc: String, lat: Double, lng: Double) {
        apiService.createMyStore(CreateStoreRequest(name, desc, lat, lng))
    }
}