// File: /ui/components/StoreCard.kt
package com.example.deliveryapp2.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.deliveryapp2.data.model.Store

@Composable
fun StoreCard(store: Store, onClick: (String) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick(store.id) },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            AsyncImage(
                model = store.imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp) // 높이 지정
                    .clip(RoundedCornerShape(8.dp)), // 둥근 모서리
                contentScale = ContentScale.Crop // 꽉 채우기
            )
            Spacer(modifier = Modifier.height(8.dp))

            Text(text = store.name, style = MaterialTheme.typography.titleMedium)
            Text(text = "⭐ ${store.rating} • ${store.deliveryTime}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Min Order: ${store.minOrderPrice} won", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }
    }
}

