package com.example.babful.data.repository

import android.util.Log // ⭐️ [신규]
import com.example.babful.data.model.ShortsItem
import com.example.babful.data.network.ApiService // ⭐️ [신규]
// ⭐️ [제거] import kotlinx.coroutines.delay
// ⭐️ [제거] import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShortsRepository @Inject constructor(
    // ⭐️ [수정] Hilt가 NetworkModule에서 만든 ApiService를 '주입'
    private val apiService: ApiService
) {

    /**
     * [수정] Go 서버 API를 호출하여 쇼츠 데이터를 가져옴
     */
    suspend fun getShortsItems(): List<ShortsItem> {

        // ⭐️ [제거] '가짜 데이터' 로직 전체 삭제
        // delay(1000)
        // return (1..20).map { ... }

        // ⭐️ [신규] '실제 API' 호출 (try-catch로 네트워크 오류 방어)
        return try {
            apiService.getShortsItems()
        } catch (e: Exception) {
            Log.e("ShortsRepository", "Go API (/shorts) 호출 실패", e)
            emptyList() // ⭐️ 오류 발생 시 빈 리스트 반환
        }
    }
}