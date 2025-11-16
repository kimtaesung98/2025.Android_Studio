package com.example.babful.ui

object NavigationRoutes {
    // ⭐️ [신규] 1. Root Route
    const val SPLASH = "splash"
    const val FEED = "feed"
    const val DELIVERY = "delivery"
    const val SHORTS = "shorts"
    const val PROFILE = "profile" // ⭐️ [신규]
    // ⭐️ [신규] 2. Authentication Routes
    const val LOGIN = "login"
    const val REGISTER = "register"
    // 3. Argument Keys
    const val ARG_STORE_ID = "storeId"

    // 4. Detail Screen Routes
    const val STORE_MENU = "store_menu/{$ARG_STORE_ID}"
    fun storeMenuRoute(storeId: String) = "store_menu/$storeId"
}