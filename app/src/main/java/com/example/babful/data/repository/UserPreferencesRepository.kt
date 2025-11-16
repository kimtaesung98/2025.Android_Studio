package com.example.babful.data.repository

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferencesRepository @Inject constructor(
    private val dataStore: DataStore<Preferences> // ⭐️ Hilt가 DataStoreModule에서 주입
) {
    // 1. DataStore에서 사용할 '키(Key)' 정의
    private object Keys {
        val JWT_TOKEN = stringPreferencesKey("jwt_token")
    }

    // 2. 토큰 저장
    suspend fun saveJwtToken(token: String) {
        dataStore.edit { preferences ->
            preferences[Keys.JWT_TOKEN] = token
            Log.d("UserPrefsRepo", "JWT 토큰 저장 완료")
        }
    }

    // 3. 토큰 읽기 (Flow)
    val jwtToken: Flow<String?> = dataStore.data
        .map { preferences ->
            preferences[Keys.JWT_TOKEN]
        }

    // 4. (참고) 토큰 즉시 1회 읽기
    suspend fun getJwtTokenOnce(): String? {
        return jwtToken.first()
    }

    // 5. 토큰 삭제 (로그아웃 시)
    suspend fun clearJwtToken() {
        dataStore.edit { preferences ->
            preferences.remove(Keys.JWT_TOKEN)
        }
    }
}