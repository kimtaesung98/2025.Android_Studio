package com.example.babful.ui.delivery

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.babful.data.model.DeliveryItem
import com.example.babful.data.repository.DeliveryRepository
import com.google.android.gms.location.*
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DeliveryUiState(
    val isLoading: Boolean = false,
    val deliveryItems: List<DeliveryItem> = emptyList(),
    val errorMessage: String? = null,
    val currentLocation: Location? = null
)

@HiltViewModel
class DeliveryViewModel @Inject constructor(
    private val deliveryRepository: DeliveryRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(DeliveryUiState())
    val uiState: StateFlow<DeliveryUiState> = _uiState.asStateFlow()

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    // ⭐️ [신규] 위치 업데이트 콜백 정의
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            locationResult.lastLocation?.let { location ->
                _uiState.update { it.copy(currentLocation = location) }
                Log.d("DeliveryViewModel", "위치 업데이트: ${location.latitude}, ${location.longitude}")
            }
        }
    }

    init {
        loadDeliveryItems()
        startLocationUpdates()
    }

    // ⭐️ [수정] SWR(Stale-While-Revalidate) 로직 적용
    private fun loadDeliveryItems() {
        Log.d("DeliveryViewModel", "[SWR] 배달 목록 로드 요청")
        _uiState.update { it.copy(isLoading = true) } // UI 스피너 시작

        viewModelScope.launch {
            // --- 1. 캐시 먼저 로드 ---
            val cacheItems = deliveryRepository.getDeliveryItemsFromCache()
            _uiState.update {
                it.copy(
                    isLoading = true, // 네트워크 요청 전까지 로딩 유지
                    deliveryItems = cacheItems
                )
            }
            Log.d("DeliveryViewModel", "[SWR] 1. 캐시 표시 완료 (아이템: ${cacheItems.size}개)")

            // --- 2. 네트워크 갱신 (try-catch) ---
            try {
                val networkItems = deliveryRepository.getDeliveryItemsFromNetwork()
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        deliveryItems = networkItems
                    )
                }
                Log.d("DeliveryViewModel", "[SWR] 2. 네트워크 갱신 완료 (아이템: ${networkItems.size}개)")
            } catch (e: Exception) {
                Log.e("DeliveryViewModel", "[SWR] 네트워크 갱신 실패", e)
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.w("DeliveryViewModel", "위치 권한 없음. 위치 업데이트 시작 불가.")
            return
        }

        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000)
            .setWaitForAccurateLocation(true)
            .setMinUpdateIntervalMillis(3000)
            .build()

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
        Log.d("DeliveryViewModel", "위치 업데이트 요청 시작.")
    }

    override fun onCleared() {
        super.onCleared()
        fusedLocationClient.removeLocationUpdates(locationCallback)
        Log.d("DeliveryViewModel", "위치 업데이트 요청 중지.")
    }
}