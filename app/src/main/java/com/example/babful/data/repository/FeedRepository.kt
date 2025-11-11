package com.example.babful.data.repository

import android.util.Log
import com.example.babful.data.model.FeedItem
import com.example.babful.data.network.ApiService // ⭐️ [신규] ApiService 임포트
// ⭐️ [제거] import kotlinx.coroutines.delay
// ⭐️ [제거] import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FeedRepository @Inject constructor(
    // ⭐️ [수정] Hilt가 NetworkModule에서 만든 ApiService를 '주입'
    private val apiService: ApiService
) {

    /**
     * [수정] Go 서버 API를 호출하여 반경 기반 피드 데이터를 가져옴
     */
    suspend fun getFeedItems(radius: Int, isRefresh: Boolean): List<FeedItem> {

        // ⭐️ [제거] '가짜 데이터' 로직 전체 삭제
        // delay(1500)
        // val itemCount = 20
        // ...

        // ⭐️ [신규] '실제 API' 호출 (try-catch로 네트워크 오류 방어)
        return try {
            // ⭐️ Retrofit이 Go 서버를 호출하고, Gson이 FeedItem 리스트로 변환
            apiService.getFeedItems(radius = radius)
        } catch (e: Exception) {
            // (White Box: Go 서버가 꺼져있거나, 10.0.2.2가 아니거나, 인터넷 권한이 없으면 여기로 옴)
            Log.e("FeedRepository", "Go API 호출 실패", e)
            emptyList() // ⭐️ 오류 발생 시 빈 리스트 반환
        }
    }
}