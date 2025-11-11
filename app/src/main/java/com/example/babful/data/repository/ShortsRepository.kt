package com.example.babful.data.repository

import android.util.Log
import com.example.babful.data.db.ShortsDao
import com.example.babful.data.model.ShortsItem
import com.example.babful.data.network.ApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShortsRepository @Inject constructor(
    private val apiService: ApiService,
    private val shortsDao: ShortsDao
) {

    /**
     * [신규] 1. 네트워크에서 쇼츠 데이터를 가져오고, Room에 캐시
     */
    suspend fun getShortsItemsFromNetwork(): List<ShortsItem> {
        return try {
            val networkItems = apiService.getShortsItems()
            Log.d("ShortsRepository", "[SWR] Go API (/shorts) 호출 성공")

            shortsDao.clearAllShorts()
            shortsDao.insertAll(networkItems)

            networkItems
        } catch (e: Exception) {
            Log.e("ShortsRepository", "[SWR] Go API 호출 실패", e)
            throw e // ⭐️ 예외 다시 던짐
        }
    }

    /**
     * [신규] 2. Room DB(캐시)에서만 쇼츠 데이터를 가져옴
     */
    suspend fun getShortsItemsFromCache(): List<ShortsItem> {
        Log.d("ShortsRepository", "[SWR] Room DB 캐시 조회")
        return shortsDao.getAllShorts()
    }

    // ⭐️ [제거] 3. 기존 23단계의 getShortsItems (try-catch 래퍼) 함수는 삭제됨
}