package com.damtoy.rewear.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.damtoy.rewear.model.UserProfile
import com.damtoy.rewear.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    object Success : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

class AuthViewModel(
    private val userRepository: UserRepository = UserRepository()
) : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState

    fun login(email: String, pass: String) {
        if (email.isBlank() || pass.isBlank()) {
            _uiState.value = AuthUiState.Error("Please fill in all fields.")
            return
        }

        _uiState.value = AuthUiState.Loading
        auth.signInWithEmailAndPassword(email, pass)
            .addOnSuccessListener { _uiState.value = AuthUiState.Success }
            .addOnFailureListener { _uiState.value = AuthUiState.Error(it.localizedMessage ?: "Login failed") }
    }

    fun register(name: String, email: String, pass: String) {
        if (name.isBlank() || email.isBlank() || pass.isBlank()) {
            _uiState.value = AuthUiState.Error("Please fill in all fields.")
            return
        }

        _uiState.value = AuthUiState.Loading
        auth.createUserWithEmailAndPassword(email, pass)
            .addOnSuccessListener { result ->
                val user = result.user
                if (user != null) {
                    val profile = UserProfile(
                        uid = user.uid,
                        displayName = name,
                        email = email,
                        ecoScore = 50 // Give them a starting baseline score!
                    )
                    viewModelScope.launch {
                        try {
                            userRepository.createUserProfile(profile)
                            _uiState.value = AuthUiState.Success
                        } catch (e: Exception) {
                            _uiState.value = AuthUiState.Error("Auth succeeded, but profile creation failed: ${e.message}")
                        }
                    }
                }
            }
            .addOnFailureListener { _uiState.value = AuthUiState.Error(it.localizedMessage ?: "Registration failed") }
    }

    fun resetState() {
        _uiState.value = AuthUiState.Idle
    }
}

class AuthViewModelFactory : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}