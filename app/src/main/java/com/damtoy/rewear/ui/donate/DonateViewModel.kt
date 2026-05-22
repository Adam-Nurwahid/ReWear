package com.damtoy.rewear.ui.donate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.damtoy.rewear.model.ClothingItem
import com.damtoy.rewear.model.Yayasan
import com.damtoy.rewear.repository.ClothingRepository
import com.damtoy.rewear.repository.UserRepository
import com.damtoy.rewear.repository.YayasanRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DonateViewModel(
    private val clothingRepository: ClothingRepository,
    private val userId: String,
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

    // Holds the user's last known GPS location — null means location not yet obtained
    private val _userLat = MutableStateFlow<Double?>(null)
    private val _userLng = MutableStateFlow<Double?>(null)

    val userLat: StateFlow<Double?> = _userLat
    val userLng: StateFlow<Double?> = _userLng

    /**
     * Called from the UI after a successful GPS fix.
     * Triggers re-sort of the foundation list.
     */
    fun updateUserLocation(lat: Double, lng: Double) {
        _userLat.value = lat
        _userLng.value = lng
    }

    /**
     * Returns foundations sorted by distance from user location (nearest first).
     * Falls back to default order if location is not yet available.
     */
    fun getFoundationsSortedByDistance(): List<Yayasan> {
        return yayasanRepository.getSortedByDistance(_userLat.value, _userLng.value)
    }

    /**
     * Distance in km from user to a specific foundation.
     * Returns null if user location is not available.
     */
    fun getDistanceKm(yayasan: Yayasan): Double? {
        val lat = _userLat.value ?: return null
        val lng = _userLng.value ?: return null
        return yayasanRepository.distanceKm(lat, lng, yayasan)
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
            @Suppress("UNCHECKED_CAST")
            return DonateViewModel(
                clothingRepository = clothingRepository,
                userId = userId
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}