package com.example.appname

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * [설계 의도 요약]
 * Hilt가 앱 전체의 의존성 주입을 관리할 수 있도록
 * 앱의 진입점(Application Class)을 설정합니다.
 */
@HiltAndroidApp // (1) 🚨 Hilt가 이 클래스를 앱의 진입점으로 인식
class MyApplication : Application() {
    // (2) Hilt가 모든 것을 처리하므로 내부는 비어있음
}