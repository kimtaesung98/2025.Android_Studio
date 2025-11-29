// File: /ui/components/CategoryChip.kt
package com.example.deliveryapp2.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CategoryChip(category: String, isSelected: Boolean = false) {
    FilterChip(
        selected = isSelected,
        onClick = { /* TODO */ },
        label = { Text(category) },
        modifier = Modifier.padding(end = 8.dp)
    )
}