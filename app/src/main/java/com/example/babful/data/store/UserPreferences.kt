package com.example.babful.data.store

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
class UserPreferences @Inject constructor(
    // ⭐️ [수정] Context 대신 DataStore를 직접 주입받습니다.
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        private val KEY_AUTH_TOKEN = stringPreferencesKey("auth_token")
    }

    suspend fun saveAuthToken(token: String) {
        dataStore.edit { preferences ->
            preferences[KEY_AUTH_TOKEN] = token
        }
    }

    val authToken: Flow<String?> = dataStore.data
        .map { preferences ->
            preferences[KEY_AUTH_TOKEN]
        }

    suspend fun getAuthTokenOnce(): String? {
        return dataStore.data
            .map { preferences -> preferences[KEY_AUTH_TOKEN] }
            .first()
    }

    suspend fun clearAuthToken() {
        dataStore.edit { preferences ->
            preferences.remove(KEY_AUTH_TOKEN)
        }
    }
}