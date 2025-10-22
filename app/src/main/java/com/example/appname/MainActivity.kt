package com.examplet.appname

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.appname.ui.theme.AppnameTheme // (1) 테마 파일 import

// (2) 메인 진입점 Activity
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // (3) 최상위 테마 적용
            AppnameTheme {
                // (4) 앱 전체 화면의 기본 배경 정의
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // (5) 여기서부터 메인 Navigation Host를 호출합니다.
                    AppMainScreen()
                }
            }
        }
    }
}

// (6) 앱의 최상위 Composeable 함수 (아직 내용은 비어있음)
@Composable
fun AppMainScreen() {
    // 여기에 Bottom Navigation과 Navigation Host가 들어갈 예정
}

// (7) 미리보기 함수
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AppnameTheme {
        AppMainScreen()
    }
}