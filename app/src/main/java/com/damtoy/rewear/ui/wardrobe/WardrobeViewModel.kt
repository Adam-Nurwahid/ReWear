package com.damtoy.rewear.ui.wardrobe


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.damtoy.rewear.model.ClothingItem
import com.damtoy.rewear.repository.ClothingRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class WardrobeViewModel(private val repository: ClothingRepository,
                        private val userId: String) : ViewModel() {
    // Automatically fetches and caches the list of clothing from Room
    val clothingList: StateFlow<List<ClothingItem>> = repository.getAllClothing(userId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun deleteClothing(item: ClothingItem) {
        viewModelScope.launch {
            repository.deleteClothing(item)
        }
    }
}

class WardrobeViewModelFactory(private val repository: ClothingRepository,
                               private val userId: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WardrobeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST") return WardrobeViewModel(repository, userId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}