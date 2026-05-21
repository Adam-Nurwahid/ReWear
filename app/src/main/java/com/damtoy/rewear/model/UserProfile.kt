package com.damtoy.rewear.model


data class UserProfile(
    val uid: String = "",
    val displayName: String = "",
    val email: String = "",
    val ecoScore: Int = 0, // Users start with 0 or a baseline score
    val totalCo2Saved: Double = 0.0,
    val donationCount: Int = 0,
    val swapCount: Int = 0
)