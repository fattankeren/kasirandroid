package com.example.kasirandroid.data.repository

import com.example.kasirandroid.data.dao.ProductDao
import com.example.kasirandroid.data.dao.TransactionDao
import com.example.kasirandroid.data.model.Product
import com.example.kasirandroid.data.model.Transaction
import com.example.kasirandroid.data.model.TransactionDetail
import com.example.kasirandroid.data.model.TransactionWithDetails
import kotlinx.coroutines.flow.Flow

class AppRepository(
    private val productDao: ProductDao,
    private val transactionDao: TransactionDao
) {
    // Products
    val allProducts: Flow<List<Product>> = productDao.getAllProducts()

    suspend fun insertProduct(product: Product) = productDao.insertProduct(product)
    suspend fun updateProduct(product: Product) = productDao.updateProduct(product)
    suspend fun deleteProduct(product: Product) = productDao.deleteProduct(product)

    // Transactions
    val allTransactions: Flow<List<TransactionWithDetails>> = transactionDao.getAllTransactionsWithDetails()

    suspend fun checkout(totalAmount: Double, items: List<Pair<Product, Int>>) {
        val transactionId = transactionDao.insertTransaction(
            Transaction(date = System.currentTimeMillis(), totalAmount = totalAmount)
        ).toInt()

        val details = items.map { (product, quantity) ->
            // Reduce stock
            productDao.reduceStock(product.id, quantity)
            
            TransactionDetail(
                transactionId = transactionId,
                productId = product.id,
                quantity = quantity,
                subtotal = product.price * quantity,
                productName = product.name
            )
        }
        transactionDao.insertTransactionDetails(details)
    }

    suspend fun nukeDatabase() {
        transactionDao.deleteAllTransactionDetails()
        transactionDao.deleteAllTransactions()
        productDao.deleteAllProducts()
    }
}
