package com.example.kasirandroid.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.kasirandroid.data.model.Product
import com.example.kasirandroid.ui.theme.KasirandroidTheme
import com.example.kasirandroid.ui.viewmodel.CartItem
import com.example.kasirandroid.ui.viewmodel.CheckoutState
import com.example.kasirandroid.ui.viewmodel.PosViewModel
import com.example.kasirandroid.ui.viewmodel.ProductViewModel

@Composable
fun KasirScreen(posViewModel: PosViewModel, productViewModel: ProductViewModel) {
    val products by productViewModel.allProducts.observeAsState(emptyList())
    val cartItems by posViewModel.cartItems.collectAsState()
    val totalAmount by posViewModel.totalAmount.collectAsState()
    val checkoutState by posViewModel.checkoutState.collectAsState()
    
    var searchQuery by remember { mutableStateOf("") }
    var showPaymentDialog by remember { mutableStateOf(false) }
    var showProductPicker by remember { mutableStateOf(false) }

    val filteredProducts = products.filter { 
        it.name.contains(searchQuery, ignoreCase = true) && it.stock > 0
    }

    Scaffold(
        topBar = {
            KasirHeader()
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // "Keranjang Belanja" Card Section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f, fill = false)
                        ) {
                            Icon(
                                Icons.Default.ShoppingCart,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Keranjang Belanja",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))
                        
                        Button(
                            onClick = { showProductPicker = true },
                            shape = CircleShape,
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.defaultMinSize(minHeight = 32.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        ) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Tambah Item",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                maxLines = 1,
                                softWrap = false
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    if (cartItems.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    Icons.Default.ShoppingBasket,
                                    contentDescription = null,
                                    modifier = Modifier.size(64.dp),
                                    tint = MaterialTheme.colorScheme.outlineVariant
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Keranjang kosong",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(cartItems) { item ->
                                ModernCartItem(
                                    item = item,
                                    onIncrement = { posViewModel.addToCart(item.product) },
                                    onDecrement = { posViewModel.removeFromCart(item.product) },
                                    onRemove = { posViewModel.removeItemCompletely(item.product) }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Bottom Action Area
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Total Pembayaran", style = MaterialTheme.typography.titleMedium)
                        Text(
                            text = "Rp ${"%,.0f".format(totalAmount)}",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                    
                    Button(
                        onClick = { showPaymentDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = cartItems.isNotEmpty(),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        Text("Bayar Sekarang", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        }

        // Dialogs
        if (showProductPicker) {
            ProductPickerDialog(
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it },
                products = filteredProducts,
                onProductSelected = { 
                    posViewModel.addToCart(it)
                },
                onDismiss = { showProductPicker = false }
            )
        }

        if (showPaymentDialog) {
            PaymentDialog(
                totalAmount = totalAmount,
                onDismiss = { showPaymentDialog = false },
                onConfirm = { cash ->
                    posViewModel.processCheckout(cash)
                    showPaymentDialog = false
                }
            )
        }

        when (val state = checkoutState) {
            is CheckoutState.Success -> {
                CheckoutSuccessDialog(
                    state = state,
                    onDismiss = { posViewModel.resetCheckoutState() }
                )
            }
            is CheckoutState.Error -> {
                AlertDialog(
                    onDismissRequest = { posViewModel.resetCheckoutState() },
                    title = { Text("Gagal") },
                    text = { Text(state.message) },
                    confirmButton = {
                        TextButton(onClick = { posViewModel.resetCheckoutState() }) {
                            Text("OK")
                        }
                    }
                )
            }
            else -> {}
        }
    }
}

@Composable
fun KasirHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        Text(
            text = "Kasir",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-0.5).sp
            ),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductPickerDialog(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    products: List<Product>,
    onProductSelected: (Product) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        dragHandle = { BottomSheetDefaults.DragHandle() },
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .fillMaxHeight(0.7f)
        ) {
            Text(
                text = "Pilih Produk",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Cari produk...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 120.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(products) { product ->
                    PosProductCard(
                        product = product,
                        onClick = { onProductSelected(product) }
                    )
                }
            }
        }
    }
}

@Composable
fun PosProductCard(product: Product, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHighest)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
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
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = product.name,
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                maxLines = 1,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Rp ${"%,.0f".format(product.price)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun ModernCartItem(
    item: CartItem,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onRemove: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.product.name,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                maxLines = 1
            )
            Text(
                text = "Rp ${"%,.0f".format(item.product.price)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            IconButton(onClick = onDecrement, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.Remove, contentDescription = null, modifier = Modifier.size(16.dp))
            }
            Text(
                text = item.quantity.toString(),
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(horizontal = 4.dp)
            )
            IconButton(onClick = onIncrement, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
            }
            Spacer(modifier = Modifier.width(4.dp))
            IconButton(onClick = onRemove, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.DeleteOutline, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
fun PaymentDialog(
    totalAmount: Double,
    onDismiss: () -> Unit,
    onConfirm: (Double) -> Unit
) {
    var cashReceived by remember { mutableStateOf("") }
    val change = (cashReceived.toDoubleOrNull() ?: 0.0) - totalAmount

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Pembayaran Tunai") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                    text = "Total Bayar: Rp ${"%,.0f".format(totalAmount)}",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                )
                OutlinedTextField(
                    value = cashReceived,
                    onValueChange = { cashReceived = it },
                    label = { Text("Uang Diterima") },
                    modifier = Modifier.fillMaxWidth(),
                    prefix = { Text("Rp ") },
                    singleLine = true
                )
                if (change >= 0) {
                    Text(
                        text = "Kembalian: Rp ${"%,.0f".format(change)}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(cashReceived.toDoubleOrNull() ?: 0.0) },
                enabled = (cashReceived.toDoubleOrNull() ?: 0.0) >= totalAmount
            ) {
                Text("Konfirmasi")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}

@Composable
fun CheckoutSuccessDialog(
    state: CheckoutState.Success,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Transaksi Berhasil!",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(24.dp))
                
                DetailRow("Total", "Rp ${"%,.0f".format(state.totalAmount)}")
                DetailRow("Bayar", "Rp ${"%,.0f".format(state.cashReceived)}")
                DetailRow("Kembalian", "Rp ${"%,.0f".format(state.change)}", isBold = true)
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Selesai")
                }
            }
        }
    }
}

@Composable
fun DetailRow(label: String, value: String, isBold: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyLarge)
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = if (isBold) FontWeight.ExtraBold else FontWeight.Medium
            )
        )
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun KasirScreenPreview() {
    KasirandroidTheme {
        Scaffold(
            topBar = { KasirHeader() }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Keranjang kosong preview")
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = {}, modifier = Modifier.fillMaxWidth()) {
                    Text("Bayar Sekarang")
                }
            }
        }
    }
}
