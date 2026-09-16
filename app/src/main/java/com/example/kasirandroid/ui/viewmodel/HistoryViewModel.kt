package com.example.kasirandroid.ui.viewmodel

import androidx.lifecycle.*
import com.example.kasirandroid.data.model.TransactionWithDetails
import com.example.kasirandroid.data.repository.AppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.util.Calendar

class HistoryViewModel(private val repository: AppRepository) : ViewModel() {
    
    private val _filter = MutableStateFlow(HistoryFilter.ALL)
    val filter: StateFlow<HistoryFilter> = _filter

    val allTransactions: StateFlow<List<TransactionWithDetails>> = repository.allTransactions
        .combine(filter) { transactions, currentFilter ->
            when (currentFilter) {
                HistoryFilter.ALL -> transactions
                HistoryFilter.TODAY -> {
                    val todayStart = Calendar.getInstance().apply {
                        set(Calendar.HOUR_OF_DAY, 0)
                        set(Calendar.MINUTE, 0)
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)
                    }.timeInMillis
                    transactions.filter { it.transaction.date >= todayStart }
                }
                HistoryFilter.LAST_7_DAYS -> {
                    val weekStart = Calendar.getInstance().apply {
                        add(Calendar.DAY_OF_YEAR, -7)
                        set(Calendar.HOUR_OF_DAY, 0)
                        set(Calendar.MINUTE, 0)
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)
                    }.timeInMillis
                    transactions.filter { it.transaction.date >= weekStart }
                }
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setFilter(newFilter: HistoryFilter) {
        _filter.value = newFilter
    }
}

enum class HistoryFilter(val label: String) {
    ALL("Semua"),
    TODAY("Hari Ini"),
    LAST_7_DAYS("7 Hari Terakhir")
}

class HistoryViewModelFactory(private val repository: AppRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HistoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HistoryViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
