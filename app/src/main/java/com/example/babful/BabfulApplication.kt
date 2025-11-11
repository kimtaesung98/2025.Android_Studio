package com.example.babful

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Hilt가 앱의 의존성 컨테이너를 생성할 수 있도록
 * Application 클래스에 @HiltAndroidApp 어노테이션을 추가합니다.
 */
@HiltAndroidApp
class BabfulApplication : Application() {
    // Hilt가 내부를 자동으로 채워주므로, 추가 코드가 필요 없습니다.
}