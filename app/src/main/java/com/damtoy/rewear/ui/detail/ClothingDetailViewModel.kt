package com.damtoy.rewear.ui.detail


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.damtoy.rewear.model.ClothingItem
import com.damtoy.rewear.repository.ClothingRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ClothingDetailViewModel(
    private val repository: ClothingRepository,
    private val itemId: Int,
    private val userId: String
) : ViewModel() {

    // Automatically fetches and observes the specific item from Room
    val clothingItem: StateFlow<ClothingItem?> = repository.getClothingById(itemId, userId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    fun wearItem(item: ClothingItem) {
        viewModelScope.launch {
            repository.incrementWearCount(item)
            // Re-wearing saves CO2 compared to buying a new item, let's credit 25% of the item's total CO2
            val userRepository = com.damtoy.rewear.repository.UserRepository()
            userRepository.incrementImpact(userId, item.totalCo2Impact * 0.25, 2)
        }
    }

    fun toggleSwap(item: ClothingItem) {
        viewModelScope.launch {
            repository.toggleSwapStatus(item)
            if (!item.isForSwap) {
                // Meaning we are marking it for swap (since it was previously false)
                val userRepository = com.damtoy.rewear.repository.UserRepository()
                userRepository.incrementImpact(userId, item.totalCo2Impact * 0.5, 5, addedDonations = 0, addedSwaps = 1)
            }
        }
    }

    fun toggleDonation(item: ClothingItem) {
        viewModelScope.launch {
            if (item.isForDonation) {
                // If it was already for donation, we unmark it. But the repository only has markForDonation.
                // I will use updateClothing to toggle it manually here.
                val updatedItem = item.copy(isForDonation = false)
                repository.updateClothing(updatedItem)
            } else {
                repository.markForDonation(item)
            }
        }
    }
}

// Factory requires the itemId passed from the Navigation arguments
class ClothingDetailViewModelFactory(
    private val repository: ClothingRepository,
    private val itemId: Int,
    private val userId: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ClothingDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST") return ClothingDetailViewModel(repository, itemId, userId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}