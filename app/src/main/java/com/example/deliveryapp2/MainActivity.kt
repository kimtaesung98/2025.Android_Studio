package com.example.deliveryapp2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.deliveryapp2.data.network.RetrofitClient
import com.example.deliveryapp2.ui.components.AppBottomBar
import com.example.deliveryapp2.ui.navigation.AppNavGraph
import com.example.deliveryapp2.ui.navigation.customerTabs
import com.example.deliveryapp2.ui.navigation.ownerTabs

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        com.example.deliveryapp2.data.network.WebSocketManager.connect()
        RetrofitClient.init(applicationContext)

        setContent {
            MaterialTheme {
                val navController = rememberNavController()

                // 현재 화면 경로(Route) 감지
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                // 하단 바를 보여줄 화면들 정의
                val showCustomerBar = currentRoute in customerTabs.map { it.route }
                val showOwnerBar = currentRoute in ownerTabs.map { it.route }

                Scaffold(
                    bottomBar = {
                        if (showCustomerBar) {
                            AppBottomBar(navController = navController, tabs = customerTabs)
                        } else if (showOwnerBar) {
                            AppBottomBar(navController = navController, tabs = ownerTabs)
                        }
                    }
                ) { innerPadding ->
                    // AppNavGraph에 padding 전달하여 콘텐츠가 바에 가려지지 않게 함
                    Surface(
                        modifier = Modifier.padding(innerPadding),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        AppNavGraph(navController = navController)
                    }
                }
            }
        }
    }
}