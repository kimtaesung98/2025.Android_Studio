// File: /data/repository/CartRepository.kt
package com.example.deliveryapp2.data.repository

import androidx.compose.runtime.mutableStateListOf
import com.example.deliveryapp2.data.model.MenuItem

// 장바구니 아이템 모델
data class CartItem(
    val menu: MenuItem,
    var quantity: Int
)

// 앱 전체에서 공유하는 장바구니 저장소 (Singleton)
object CartRepository {
    // UI 업데이트를 위해 mutableStateListOf 사용
    private val _items = mutableStateListOf<CartItem>()
    val items: List<CartItem> get() = _items

    fun addToCart(menu: MenuItem) {
        val existingItem = _items.find { it.menu.id == menu.id }
        if (existingItem != null) {
            existingItem.quantity++
        } else {
            _items.add(CartItem(menu, 1))
        }
    }

    fun removeMenu(menu: MenuItem) {
        val existingItem = _items.find { it.menu.id == menu.id }
        if (existingItem != null) {
            if (existingItem.quantity > 1) {
                existingItem.quantity--
            } else {
                _items.remove(existingItem)
            }
        }
    }

    fun clearCart() {
        _items.clear()
    }

    fun getTotalPrice(): Int {
        return _items.sumOf { it.menu.price * it.quantity }
    }
}