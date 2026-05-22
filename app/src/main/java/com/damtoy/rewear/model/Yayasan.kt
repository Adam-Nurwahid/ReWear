package com.damtoy.rewear.model

data class Yayasan(
    val id: String,
    val name: String,
    val city: String,
    val address: String,
    val phone: String,
    val waLink: String,
    val acceptedCategories: List<ClothingCategory>,
    val latitude: Double,       // GPS coordinate for distance calculation
    val longitude: Double,      // GPS coordinate for distance calculation
    val description: String,    // Short description of the foundation
    val website: String? = null,
    val logoRes: Int? = null    // For local drawable resources
)