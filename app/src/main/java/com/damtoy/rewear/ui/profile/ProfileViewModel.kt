package com.damtoy.rewear.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.damtoy.rewear.model.UserProfile
import com.damtoy.rewear.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class ProfileUiState {
    object Loading : ProfileUiState()
    data class Success(val profile: UserProfile) : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
}

class ProfileViewModel(
    private val userRepository: UserRepository = UserRepository()
) : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState: StateFlow<ProfileUiState> = _uiState

    init {
        loadProfile()
    }

    fun loadProfile() {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            _uiState.value = ProfileUiState.Error("User not logged in")
            return
        }

        _uiState.value = ProfileUiState.Loading
        viewModelScope.launch {
            try {
                userRepository.getUserProfileFlow(uid).collect { profile ->
                    if (profile != null) {
                        _uiState.value = ProfileUiState.Success(profile)
                    } else {
                        _uiState.value = ProfileUiState.Error("Profile not found")
                    }
                }
            } catch (e: Exception) {
                _uiState.value = ProfileUiState.Error(e.localizedMessage ?: "Failed to load profile")
            }
        }
    }

    fun logout() {
        auth.signOut()
    }
}

class ProfileViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProfileViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
