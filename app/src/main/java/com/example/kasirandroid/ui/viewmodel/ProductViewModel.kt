package com.example.kasirandroid.ui.viewmodel

import androidx.lifecycle.*
import com.example.kasirandroid.data.model.Product
import com.example.kasirandroid.data.repository.AppRepository
import kotlinx.coroutines.launch

class ProductViewModel(private val repository: AppRepository) : ViewModel() {
    val allProducts: LiveData<List<Product>> = repository.allProducts.asLiveData()

    fun addProduct(name: String, price: Double, stock: Int, category: String) {
        viewModelScope.launch {
            repository.insertProduct(Product(name = name, price = price, stock = stock, category = category))
        }
    }

    fun updateProduct(product: Product) {
        viewModelScope.launch {
            repository.updateProduct(product)
        }
    }

    fun deleteProduct(product: Product) {
        viewModelScope.launch {
            try {
                repository.deleteProduct(product)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun resetDatabase() {
        viewModelScope.launch {
            repository.nukeDatabase()
        }
    }
}

class ProductViewModelFactory(private val repository: AppRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProductViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
