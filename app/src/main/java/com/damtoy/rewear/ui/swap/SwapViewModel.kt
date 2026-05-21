package com.damtoy.rewear.ui.swap


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.damtoy.rewear.model.ClothingItem
import com.damtoy.rewear.repository.ClothingRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SwapViewModel(private val repository: ClothingRepository,
                    private val userId: String) : ViewModel() {

    // Observes only items where isForSwap == true
    val swapList: StateFlow<List<ClothingItem>> = repository.getSwapClothing(userId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun unmarkForSwap(item: ClothingItem) {
        viewModelScope.launch {
            repository.toggleSwapStatus(item)
        }
    }
}

class SwapViewModelFactory(private val repository: ClothingRepository,
                           private val userId: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SwapViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST") return SwapViewModel(repository, userId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}