// File: MainActivity.kt
package com.example.deliveryapp2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.navigation.compose.rememberNavController
import com.example.deliveryapp2.ui.navigation.AppNavGraph

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()
                    // 실제 앱에서는 Hilt/Koin 등 DI 라이브러리를 사용해 ViewModel 주입을 권장하지만,
                    // AutoBuild 데모를 위해 NavGraph 내부에서 직접 인스턴스화 하거나 팩토리를 사용합니다.
                    AppNavGraph(navController = navController)
                }
            }
        }
    }
}