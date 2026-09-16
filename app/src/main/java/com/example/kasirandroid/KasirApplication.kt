package com.example.kasirandroid

import android.app.Application
import com.example.kasirandroid.data.AppDatabase
import com.example.kasirandroid.data.repository.AppRepository

class KasirApplication : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy { AppRepository(database.productDao(), database.transactionDao()) }
}
