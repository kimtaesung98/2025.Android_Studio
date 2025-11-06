plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    // (1) 🚨 Kapt 플러그인 (필수)
    id("org.jetbrains.kotlin.kapt")

    // (2) 🚨 Hilt 플러그인
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.example.appname"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.appname"
        minSdk = 29
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
    buildFeatures {
        buildConfig = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    val nav_version = "2.9.5"
    // Jetpack Compose integration
    implementation("androidx.navigation:navigation-compose:$nav_version")

    // Views/Fragments integration
    implementation("androidx.navigation:navigation-fragment:$nav_version")
    implementation("androidx.navigation:navigation-ui:$nav_version")

    // Feature module support for Fragments
    implementation("androidx.navigation:navigation-dynamic-features-fragment:$nav_version")

    // Testing Navigation
    androidTestImplementation("androidx.navigation:navigation-testing:$nav_version")

    // JSON serialization library, works with the Kotlin serialization plugin
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")

    // (1) 🚨 ViewModel을 Compose에서 사용하기 위한 라이브러리
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.0")

// 이전에 추가했던 navigation-compose 라이브러리
    implementation("androidx.navigation:navigation-compose:2.7.7")

    // (1) 🚨 Material Design 아이콘을 사용하기 위한 라이브러리
    implementation("androidx.compose.material:material-icons-extended")

    // ... dependencies 블록의 다른 내용들 ...

// (1) 🚨 Pager (Horizontal, Vertical) 기능을 위한 라이브러리
    implementation("androidx.compose.foundation:foundation:1.6.7") // foundation의 Pager가 1.6.0부터 정식 포함됨

// (2) 🚨 비디오 재생(ExoPlayer)을 위한 Media3 라이브러리
    implementation("androidx.media3:media3-exoplayer:1.3.1")
    implementation("androidx.media3:media3-ui:1.3.1") // 플레이어 UI 컨트롤러

    // (3) 🚨 Hilt 의존성 추가
    implementation("com.google.dagger:hilt-android:2.51.1")
    kapt("com.google.dagger:hilt-android-compiler:2.51.1")
    // (4) 🚨 ViewModel을 Hilt로 주입하기 위한 추가 라이브러리
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    // (1) 🚨 Jetpack DataStore (Preferences) 라이브러리 추가
    implementation("androidx.datastore:datastore-preferences:1.1.1")

    // (2) 🚨 Retrofit2 (네트워크 클라이언트)
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    // (3) 🚨 Moshi (JSON <-> Kotlin 변환기)
    implementation("com.squareup.retrofit2:converter-moshi:2.9.0")
    implementation("com.squareup.moshi:moshi-kotlin:1.15.0")
    kapt("com.squareup.moshi:moshi-kotlin-codegen:1.15.0") // Moshi 코드 생성을 위함
    // (4) 🚨 (선택적이지만 권장) 네트워크 통신 로깅
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")

    // (1) 🚨 Room 라이브러리 추가
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1") // 코루틴(Flow, suspend) 지원
    kapt("androidx.room:room-compiler:2.6.1") // Room 코드 생성기



}