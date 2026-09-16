package com.example.kasirandroid.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kasirandroid.data.model.TransactionDetail
import com.example.kasirandroid.data.model.TransactionWithDetails
import com.example.kasirandroid.ui.theme.KasirandroidTheme
import com.example.kasirandroid.ui.viewmodel.HistoryFilter
import com.example.kasirandroid.ui.viewmodel.HistoryViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HistoryScreen(viewModel: HistoryViewModel) {
    val transactions by viewModel.allTransactions.collectAsState()
    val currentFilter by viewModel.filter.collectAsState()
    
    var selectedTransaction by remember { mutableStateOf<TransactionWithDetails?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        HistoryHeader()
        
        FilterSection(
            selectedFilter = currentFilter,
            onFilterSelected = { viewModel.setFilter(it) }
        )

        if (transactions.isEmpty()) {
            EmptyHistoryState()
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(transactions) { transaction ->
                    RiwayatItemCard(
                        transaction = transaction,
                        onClick = { selectedTransaction = transaction }
                    )
                }
            }
        }
    }

    selectedTransaction?.let { transaction ->
        ReceiptDialog(
            transaction = transaction,
            onDismiss = { selectedTransaction = null }
        )
    }
}

@Composable
fun HistoryHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        Text(
            text = "Riwayat Transaksi",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-0.5).sp
            ),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun FilterSection(
    selectedFilter: HistoryFilter,
    onFilterSelected: (HistoryFilter) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(bottom = 8.dp)
    ) {
        items(HistoryFilter.values()) { filter ->
            FilterChip(
                selected = selectedFilter == filter,
                onClick = { onFilterSelected(filter) },
                label = { Text(filter.label) },
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
fun RiwayatItemCard(
    transaction: TransactionWithDetails,
    onClick: () -> Unit
) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
    val dateString = dateFormat.format(Date(transaction.transaction.date))

    ElevatedCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.ReceiptLong,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = dateString,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${transaction.details.size} Item",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "Rp ${"%,.0f".format(transaction.transaction.totalAmount)}",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                )
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFE8F5E9),
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Text(
                        text = "Lunas",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF2E7D32),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ReceiptDialog(
    transaction: TransactionWithDetails,
    onDismiss: () -> Unit
) {
    val dateFormat = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale.getDefault())
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Detail Transaksi",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = dateFormat.format(Date(transaction.transaction.date)),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                
                transaction.details.forEach { detail ->
                    ReceiptItemRow(detail)
                }
                
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Total", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    Text(
                        text = "Rp ${"%,.0f".format(transaction.transaction.totalAmount)}",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                Text("Tutup")
            }
        }
    )
}

@Composable
fun ReceiptItemRow(detail: TransactionDetail) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = detail.productName, style = MaterialTheme.typography.bodyMedium)
            Text(
                text = "${detail.quantity} x Rp ${"%,.0f".format(detail.subtotal / detail.quantity)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            text = "Rp ${"%,.0f".format(detail.subtotal)}",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
        )
    }
}

@Composable
fun EmptyHistoryState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                Icons.Default.ReceiptLong,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.outlineVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Belum ada transaksi",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RiwayatItemPreview() {
    KasirandroidTheme {
        // Mock data
        val mockTransaction = TransactionWithDetails(
            transaction = com.example.kasirandroid.data.model.Transaction(date = System.currentTimeMillis(), totalAmount = 50000.0),
            details = listOf(
                TransactionDetail(transactionId = 1, productId = 1, quantity = 2, subtotal = 40000.0, productName = "Kopi Latte"),
                TransactionDetail(transactionId = 1, productId = 2, quantity = 1, subtotal = 10000.0, productName = "Donut")
            )
        )
        Box(modifier = Modifier.padding(16.dp)) {
            RiwayatItemCard(transaction = mockTransaction, onClick = {})
        }
    }
}
