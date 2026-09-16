package com.example.kasirandroid.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
fun ProductInfo(name: String, price: Double, stock: Int) {
    Column {
        Text(text = name, style = MaterialTheme.typography.bodyLarge)
        Text(
            text = "Rp ${"%,.0f".format(price)}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Text(text = "Stok: $stock", style = MaterialTheme.typography.bodySmall)
    }
}
