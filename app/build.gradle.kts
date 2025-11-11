plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    // ⭐️ [신규] 'kotlin-kapt' 추가 (Hilt가 코드를 생성하기 위해 필요)
    id("kotlin-kapt")

    // ⭐️ [신규] Hilt 플러그인 적용
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.example.babful"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.babful"
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
    // Jetpack Compose Navigation
    implementation("androidx.navigation:navigation-compose:2.7.7") // (버전은 최신으로)

    // Material 3 (Scaffold, NavigationBar 등을 위함)
    implementation("androidx.compose.material3:material3:1.2.1") // (버전은 최신으로)

    // Icons (Home, List 등 아이콘 사용을 위함)
    implementation("androidx.compose.material:material-icons-extended:1.6.7") // (버전은 최신으로)
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0") // (버전은 최신으로)

    // collectAsStateWithLifecycle 사용을 위함
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")
    implementation("androidx.compose.foundation:foundation:1.6.7")
    implementation("io.coil-kt:coil-compose:2.6.0")
    // ⭐️ [신규] Hilt (DI)
    implementation("com.google.dagger:hilt-android:2.51.1")
    kapt("com.google.dagger:hilt-android-compiler:2.51.1")

    // ⭐️ [신규] Composable에서 hiltViewModel()을 사용하기 위함
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
    // ⭐️ [신규] Retrofit (HTTP 클라이언트)
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    // ⭐️ [신규] Gson (JSON <-> Kotlin 변환기)
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    // ⭐️ [신규] Room (로컬 DB)
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1") // 코루틴 지원
    kapt("androidx.room:room-compiler:2.6.1")
    // ⭐️ [신규] JUnit 4 (기본 테스트 프레임워크)
    testImplementation("junit:junit:4.13.2")

    // ⭐️ [신규] Kotlinx Coroutines Test (runTest)
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")

    // ⭐️ [신규] MockK (모킹 라이브러리)
    testImplementation("io.mockk:mockk:1.13.11")

    // ⭐️ [신규] Turbine (Flow 테스트용 - ViewModel 테스트 시 필요)
    testImplementation("app.cash.turbine:turbine:1.1.0")

    // ⭐️ [신규] Truth (Google의 Assert 라이브러리)
    testImplementation("com.google.truth:truth:1.4.2")

}
// ⭐️ [신규] Hilt 플러그인을 kapt에 적용
kapt {
    correctErrorTypes = true
}