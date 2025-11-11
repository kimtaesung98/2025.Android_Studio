package com.example.babful.ui

// 앱의 모든 네비게이션 경로를 관리하는 객체
object NavigationRoutes {
    // 1. Bottom Navigation Routes
    const val FEED = "feed"
    const val DELIVERY = "delivery"
    const val SHORTS = "shorts"

    // 2. Argument Keys
    const val ARG_STORE_ID = "storeId"

    // 3. Detail Screen Routes
    // (예: "store_menu/{storeId}")
    const val STORE_MENU = "store_menu/{$ARG_STORE_ID}"

    // 4. Helper function to create route with arguments
    fun storeMenuRoute(storeId: String) = "store_menu/$storeId"
}