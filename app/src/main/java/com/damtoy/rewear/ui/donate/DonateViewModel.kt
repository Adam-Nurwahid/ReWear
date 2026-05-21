package com.damtoy.rewear.ui.donate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.damtoy.rewear.model.ClothingItem
import com.damtoy.rewear.model.Yayasan
import com.damtoy.rewear.repository.ClothingRepository
import com.damtoy.rewear.repository.UserRepository
import com.damtoy.rewear.repository.YayasanRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DonateViewModel(
    private val clothingRepository: ClothingRepository,
    private val userId: String, // Kept this in second position to match factory logic
    private val yayasanRepository: YayasanRepository = YayasanRepository(),
    private val userRepository: UserRepository = UserRepository()
) : ViewModel() {

    // Streams clothes marked with isForDonation = true from our Room layer
    val donationList: StateFlow<List<ClothingItem>> = clothingRepository.getDonationClothing(userId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun getAvailableFoundations(): List<Yayasan> {
        return yayasanRepository.getAllYayasan()
    }

    fun confirmDonationRealized(item: ClothingItem, yayasanId: String) {
        viewModelScope.launch {
            // Assign the target Foundation ID to the local tracking entry
            val updatedItem = item.copy(donationYayasanId = yayasanId)
            clothingRepository.updateClothing(updatedItem)

            // Clear item out of active inventory tracking
            clothingRepository.deleteClothing(updatedItem)

            // Boost user's Firestore Eco Score by 10 points and add the CO2 impact they saved by donating it
            userRepository.incrementImpact(userId, item.totalCo2Impact * 0.8, 10, addedDonations = 1, addedSwaps = 0)
        }
    }
}

class DonateViewModelFactory(
    private val clothingRepository: ClothingRepository,
    private val userId: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DonateViewModel::class.java)) {
            // FIXED: Explicitly passes arguments in the exact matching constructor layout sequence
            @Suppress("UNCHECKED_CAST")
            return DonateViewModel(
                clothingRepository = clothingRepository,
                userId = userId
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}