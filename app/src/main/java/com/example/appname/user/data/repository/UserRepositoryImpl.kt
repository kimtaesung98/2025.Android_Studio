package com.example.appname.user.data.repository

import com.example.appname.user.data.local.UserPreferencesRepository
import com.example.appname.user.domain.model.User
import com.example.appname.user.domain.repository.UserRepository
import kotlinx.coroutines.delay
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
class UserRepositoryImpl @Inject constructor( // 🚨 (2) [Update] 생성자 주입
    private val userPreferences: UserPreferencesRepository
) : UserRepository {

    override suspend fun login(email: String, password: String): Result<User> {
        // ... (기존 API 호출 시뮬레이션) ...
        delay(1000)
        val dummyUser = User(id = "uid-123", email = email, nickname = "테스트 유저")

        // 🚨 (3) [New] 로그인 성공 시, 토큰(여기선 ID)을 DataStore에 저장
        saveAuthToken(dummyUser.id) // 👈 UseCase가 아닌 Repository가 직접 호출

        return Result.success(dummyUser)
    }

    override suspend fun logout(): Result<Boolean> {
        // ... (기존 로그아웃 시뮬레이션) ...

        // 🚨 (4) [New] 로그아웃 성공 시, DataStore에서 토큰 삭제
        saveAuthToken(null)

        return Result.success(true)
    }

    // 🚨 (5) [New] DataStore Wrapper의 Flow를 그대로 반환
    override fun getAuthTokenFlow(): Flow<String?> {
        return userPreferences.authTokenFlow
    }

    // 🚨 (6) [New] DataStore Wrapper의 save 함수 호출
    override suspend fun saveAuthToken(token: String?) {
        userPreferences.saveAuthToken(token)
    }

    // 🚨 (7) [New] 토큰으로 사용자 정보 가져오기 (시뮬레이션)
    override suspend fun getUserProfile(token: String): Result<User> {
        // TODO: 실제로는 API로 토큰을 보내 사용자 정보를 받아와야 함
        delay(500)
        if (token == "uid-123") {
            return Result.success(User(id = "uid-123", email = "test@user.com", nickname = "테스트 유저"))
        }
        return Result.failure(Exception("유효하지 않은 토큰"))
    }
}