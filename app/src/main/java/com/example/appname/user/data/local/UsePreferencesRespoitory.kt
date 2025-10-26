package com.example.appname.user.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton
// (1) DataStore 인스턴스 생성 (Context 확장 기능)
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

/**
 * [설계 의도 요약]
 * DataStore에 사용자 인증 토큰(또는 ID)을 저장하고 읽어옵니다.
 * 이 클래스는 Data Layer에 속하며 RepositoryImpl에 주입됩니다.
 */
@Singleton
class UserPreferencesRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    // (2) 저장할 데이터의 '키' 정의
    companion object {
        val KEY_AUTH_TOKEN = stringPreferencesKey("auth_token")
    }

    // (3) 토큰을 DataStore에 저장하는 함수 (suspend)
    suspend fun saveAuthToken(token: String?) {
        context.dataStore.edit { preferences ->
            if (token == null) {
                preferences.remove(KEY_AUTH_TOKEN) // 토큰이 null이면 삭제 (로그아웃)
            } else {
                preferences[KEY_AUTH_TOKEN] = token
            }
        }
    }

    // (4) 저장된 토큰을 Flow로 읽어오는 함수 (실시간 감지)
    val authTokenFlow: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[KEY_AUTH_TOKEN]
        }
}