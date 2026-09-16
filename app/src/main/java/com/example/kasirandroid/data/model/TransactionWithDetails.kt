package com.example.kasirandroid.data.model

import androidx.room.Embedded
import androidx.room.Relation

data class TransactionWithDetails(
    @Embedded val transaction: Transaction,
    @Relation(
        parentColumn = "id",
        entityColumn = "transactionId"
    )
    val details: List<TransactionDetail>
)
