package com.example.babful // ⭐️ 패키지명 확인

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/**
 * [신규] ViewModel의 Main Dispatcher를 테스트용 Dispatcher로 교체하는 JUnit 규칙
 */
@ExperimentalCoroutinesApi
class TestDispatchersRule(
    val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()
) : TestWatcher() {

    // 테스트 시작 전: Main Dispatcher를 가짜(testDispatcher)로 설정
    override fun starting(description: Description) {
        Dispatchers.setMain(testDispatcher)
    }

    // 테스트 종료 후: Main Dispatcher를 원래대로 복구
    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}