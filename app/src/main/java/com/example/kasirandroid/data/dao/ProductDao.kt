package com.example.kasirandroid.data.dao

import androidx.room.*
import com.example.kasirandroid.data.model.Product
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Query("SELECT * FROM products ORDER BY name ASC")
    fun getAllProducts(): Flow<List<Product>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: Product)

    @Update
    suspend fun updateProduct(product: Product)

    @Delete
    suspend fun deleteProduct(product: Product)

    @Query("SELECT * FROM products WHERE id = :id")
    suspend fun getProductById(id: Int): Product?

    @Query("UPDATE products SET stock = stock - :quantity WHERE id = :productId")
    suspend fun reduceStock(productId: Int, quantity: Int)

    @Query("DELETE FROM products")
    suspend fun deleteAllProducts()
}
