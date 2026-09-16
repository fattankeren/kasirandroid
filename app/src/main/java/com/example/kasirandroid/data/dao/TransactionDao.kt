package com.example.kasirandroid.data.dao

import androidx.room.*
import com.example.kasirandroid.data.model.Transaction
import com.example.kasirandroid.data.model.TransactionDetail
import com.example.kasirandroid.data.model.TransactionWithDetails
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @androidx.room.Transaction
    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun getAllTransactionsWithDetails(): Flow<List<TransactionWithDetails>>

    @Insert
    suspend fun insertTransaction(transaction: Transaction): Long

    @Insert
    suspend fun insertTransactionDetails(details: List<TransactionDetail>)

    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getTransactionById(id: Int): Transaction?

    @Query("DELETE FROM transaction_details")
    suspend fun deleteAllTransactionDetails()

    @Query("DELETE FROM transactions")
    suspend fun deleteAllTransactions()
}
