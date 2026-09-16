package com.example.kasirandroid.ui.viewmodel

import androidx.lifecycle.*
import com.example.kasirandroid.data.model.Product
import com.example.kasirandroid.data.repository.AppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class CartItem(val product: Product, val quantity: Int)

class PosViewModel(private val repository: AppRepository) : ViewModel() {
    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems

    private val _checkoutState = MutableStateFlow<CheckoutState>(CheckoutState.Idle)
    val checkoutState: StateFlow<CheckoutState> = _checkoutState

    val totalAmount: StateFlow<Double> = _cartItems
        .map { items -> items.sumOf { it.product.price * it.quantity } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    fun addToCart(product: Product) {
        val currentItems = _cartItems.value.toMutableList()
        val index = currentItems.indexOfFirst { it.product.id == product.id }
        if (index != -1) {
            val item = currentItems[index]
            if (item.quantity < product.stock) {
                currentItems[index] = item.copy(quantity = item.quantity + 1)
            }
        } else {
            if (product.stock > 0) {
                currentItems.add(CartItem(product, 1))
            }
        }
        _cartItems.value = currentItems
    }

    fun removeFromCart(product: Product) {
        val currentItems = _cartItems.value.toMutableList()
        val index = currentItems.indexOfFirst { it.product.id == product.id }
        if (index != -1) {
            val item = currentItems[index]
            if (item.quantity > 1) {
                currentItems[index] = item.copy(quantity = item.quantity - 1)
            } else {
                currentItems.removeAt(index)
            }
        }
        _cartItems.value = currentItems
    }

    fun removeItemCompletely(product: Product) {
        val currentItems = _cartItems.value.filter { it.product.id != product.id }
        _cartItems.value = currentItems
    }

    fun clearCart() {
        _cartItems.value = emptyList()
        _checkoutState.value = CheckoutState.Idle
    }

    fun processCheckout(cashReceived: Double) {
        val total = totalAmount.value
        if (cashReceived < total) {
            _checkoutState.value = CheckoutState.Error("Uang tunai tidak cukup")
            return
        }

        viewModelScope.launch {
            try {
                val items = _cartItems.value.map { it.product to it.quantity }
                repository.checkout(total, items)
                _checkoutState.value = CheckoutState.Success(
                    totalAmount = total,
                    cashReceived = cashReceived,
                    change = cashReceived - total
                )
                _cartItems.value = emptyList()
            } catch (e: Exception) {
                _checkoutState.value = CheckoutState.Error(e.message ?: "Terjadi kesalahan")
            }
        }
    }

    fun resetCheckoutState() {
        _checkoutState.value = CheckoutState.Idle
    }
}

sealed class CheckoutState {
    object Idle : CheckoutState()
    data class Success(val totalAmount: Double, val cashReceived: Double, val change: Double) : CheckoutState()
    data class Error(val message: String) : CheckoutState()
}

class PosViewModelFactory(private val repository: AppRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PosViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PosViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
