package com.damtoy.rewear.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.damtoy.rewear.domain.WardrobeSummary
import com.damtoy.rewear.repository.ClothingRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn


sealed class DashboardUiState {
    object Loading : DashboardUiState()
    data class Success(
        val userProfile: com.damtoy.rewear.model.UserProfile?,
        val summary: WardrobeSummary,
        val tips: List<String>
    ) : DashboardUiState()
}

class DashboardViewModel(
    private val repository: ClothingRepository,
    private val userRepository: com.damtoy.rewear.repository.UserRepository,
    private val userId: String
) : ViewModel() {

    // Fetch the user profile real-time as a flow
    private val userProfileFlow = userRepository.getUserProfileFlow(userId)

    // Combine multiple flows from the repository into a single UI state stream
    val uiState: StateFlow<DashboardUiState> = combine(
        userProfileFlow,
        repository.getWardrobeSummary(userId),
        repository.getEcoTips(userId)
    ) { profile, summary, tips ->
        DashboardUiState.Success(profile, summary, tips)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DashboardUiState.Loading
    )
}

// Factory to inject the ClothingRepository without Hilt/Dagger
class DashboardViewModelFactory(
    private val repository: ClothingRepository,
    private val userId: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DashboardViewModel(repository, com.damtoy.rewear.repository.UserRepository(), userId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}