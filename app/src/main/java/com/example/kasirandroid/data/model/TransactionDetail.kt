package com.example.kasirandroid.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "transaction_details",
    foreignKeys = [
        ForeignKey(
            entity = Transaction::class,
            parentColumns = ["id"],
            childColumns = ["transactionId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Product::class,
            parentColumns = ["id"],
            childColumns = ["productId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class TransactionDetail(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val transactionId: Int,
    val productId: Int,
    val quantity: Int,
    val subtotal: Double,
    val productName: String // Denormalized for history view
)
