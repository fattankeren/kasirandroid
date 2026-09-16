package com.example.kasirandroid.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kasirandroid.data.model.Product
import com.example.kasirandroid.ui.theme.KasirandroidTheme
import com.example.kasirandroid.ui.viewmodel.ProductViewModel

@Composable
fun ProdukScreen(viewModel: ProductViewModel) {
    val products by viewModel.allProducts.observeAsState(emptyList())
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Semua") }
    
    var showAddDialog by remember { mutableStateOf(false) }
    var productToEdit by remember { mutableStateOf<Product?>(null) }

    val filteredProducts = products.filter { product ->
        val matchesSearch = product.name.contains(searchQuery, ignoreCase = true)
        val matchesCategory = selectedCategory == "Semua" || product.category == selectedCategory
        matchesSearch && matchesCategory
    }

    Scaffold(
        topBar = {
            ProdukTopBar(
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it },
                onResetClick = { viewModel.resetDatabase() }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(16.dp),
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 4.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Produk")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.surface)
        ) {
            CategoryFilters(
                selectedCategory = selectedCategory,
                onCategorySelected = { selectedCategory = it }
            )
            
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    Text(
                        text = if (searchQuery.isEmpty()) "Daftar Produk" else "Hasil Pencarian",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
                
                items(filteredProducts, key = { it.id }) { product ->
                    ModernProductCard(
                        product = product,
                        onEdit = { productToEdit = product },
                        onDelete = { viewModel.deleteProduct(product) }
                    )
                }
                
                if (filteredProducts.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Tidak ada produk ditemukan",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        ProductFormDialog(
            title = "Tambah Produk",
            onDismiss = { showAddDialog = false },
            onConfirm = { name, price, stock, category ->
                viewModel.addProduct(name, price, stock, category)
                showAddDialog = false
            }
        )
    }

    productToEdit?.let { product ->
        ProductFormDialog(
            title = "Edit Produk",
            product = product,
            onDismiss = { productToEdit = null },
            onConfirm = { name, price, stock, category ->
                viewModel.updateProduct(product.copy(name = name, price = price, stock = stock, category = category))
                productToEdit = null
            }
        )
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProdukTopBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onResetClick: () -> Unit
) {
    var showResetDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Katalog Produk",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-0.5).sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            
            IconButton(onClick = { showResetDialog = true }) {
                Icon(
                    Icons.Default.Restore, 
                    contentDescription = "Reset Data",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        TextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp)),
            placeholder = { Text("Cari produk Anda...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ),
            singleLine = true
        )
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Reset Semua Data") },
            text = { Text("Ini akan menghapus SELURUH produk dan riwayat transaksi. Tindakan ini tidak bisa dibatalkan.") },
            confirmButton = {
                Button(
                    onClick = {
                        onResetClick()
                        showResetDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Hapus Semua")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }
}

@Composable
fun CategoryFilters(
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
) {
    val categories = listOf("Semua", "Makanan", "Minuman", "Elektronik", "Pakaian", "Lainnya")

    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        items(categories) { category ->
            FilterChip(
                selected = selectedCategory == category,
                onClick = { onCategorySelected(category) },
                label = { Text(category) },
                shape = CircleShape,
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                border = null
            )
        }
    }
}

@Composable
fun ModernProductCard(
    product: Product,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = product.name.take(1).uppercase(),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    maxLines = 1
                )
                Text(
                    text = "Rp ${"%,.0f".format(product.price)}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = product.category,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", modifier = Modifier.size(20.dp))
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.error)
                    }
                }
                val stockColor = if (product.stock < 5) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                Text(
                    text = "Stok: ${product.stock}",
                    style = MaterialTheme.typography.bodySmall,
                    color = stockColor,
                    fontWeight = if (product.stock < 5) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}

@Composable
fun ProductFormDialog(
    title: String,
    product: Product? = null,
    onDismiss: () -> Unit,
    onConfirm: (String, Double, Int, String) -> Unit
) {
    var name by remember { mutableStateOf(product?.name ?: "") }
    var price by remember { mutableStateOf(product?.price?.toLong()?.toString() ?: "") }
    var stock by remember { mutableStateOf(product?.stock?.toString() ?: "") }
    var category by remember { mutableStateOf(product?.category ?: "Lainnya") }

    val categories = listOf("Makanan", "Minuman", "Elektronik", "Pakaian", "Lainnya")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nama Produk") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Harga") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = stock,
                    onValueChange = { stock = it },
                    label = { Text("Stok Awal") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Text(text = "Kategori", style = MaterialTheme.typography.labelLarge)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(categories) { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(cat) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val p = price.toDoubleOrNull() ?: 0.0
                    val s = stock.toIntOrNull() ?: 0
                    onConfirm(name, p, s, category)
                }
            ) {
                Text("Simpan")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun FullProdukScreenPreview() {
    KasirandroidTheme {
        // We can't easily mock the ViewModel for a full preview without more setup,
        // but we can preview the layout components.
        Scaffold(
            topBar = {
                ProdukTopBar(searchQuery = "", onSearchQueryChange = {}, onResetClick = {})
            }
        ) { padding ->
            Column(modifier = Modifier.padding(padding)) {
                CategoryFilters(selectedCategory = "Semua", onCategorySelected = {})
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(3) {
                        ModernProductCard(
                            product = Product(name = "Produk Sample $it", price = 25000.0, stock = 10, category = "Makanan"),
                            onEdit = {},
                            onDelete = {}
                        )
                    }
                }
            }
        }
    }
}
