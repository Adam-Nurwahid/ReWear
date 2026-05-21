package com.damtoy.rewear.ui.outfit

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.damtoy.rewear.model.ClothingItem
import com.damtoy.rewear.network.GeminiOutfitService
import com.damtoy.rewear.network.OutfitSuggestion
import com.damtoy.rewear.repository.ClothingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

sealed class OutfitUiState {
    object Idle : OutfitUiState()
    object Loading : OutfitUiState()
    data class Success(val suggestions: List<OutfitSuggestion>, val wardrobe: List<ClothingItem>) : OutfitUiState()
    data class Error(val message: String) : OutfitUiState()
}

class OutfitViewModel(
    private val repository: ClothingRepository,
    private val userId: String,
    context: Context
) : ViewModel() {

    private val outfitService = GeminiOutfitService(context)

    private val _uiState = MutableStateFlow<OutfitUiState>(OutfitUiState.Idle)
    val uiState: StateFlow<OutfitUiState> = _uiState

    fun generateOutfits() {
        _uiState.value = OutfitUiState.Loading

        viewModelScope.launch {
            try {
                // Fetch the latest clothing collection from the repository
                val wardrobe = repository.getAllClothing(userId).first()

                if (wardrobe.isEmpty()) {
                    _uiState.value = OutfitUiState.Error("Your wardrobe is empty. Add some clothes in the Wardrobe tab first!")
                    return@launch
                }

                // Execute the multimodal Gemini generation
                val recommendations = outfitService.generateMultimodalOutfits(wardrobe)
                _uiState.value = OutfitUiState.Success(recommendations, wardrobe)

            } catch (e: Exception) {
                _uiState.value = OutfitUiState.Error(e.localizedMessage ?: "Failed to generate outfit combinations.")
            }
        }
    }
}

class OutfitViewModelFactory(
    private val repository: ClothingRepository,
    private val userId: String,
    private val context: Context // Required to pass context to the service layer
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OutfitViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST") return OutfitViewModel(repository, userId, context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}