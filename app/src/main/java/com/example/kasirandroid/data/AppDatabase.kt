package com.example.kasirandroid.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.kasirandroid.data.dao.ProductDao
import com.example.kasirandroid.data.dao.TransactionDao
import com.example.kasirandroid.data.model.Product
import com.example.kasirandroid.data.model.Transaction
import com.example.kasirandroid.data.model.TransactionDetail

@Database(
    entities = [Product::class, Transaction::class, TransactionDetail::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun transactionDao(): TransactionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "kasir_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
