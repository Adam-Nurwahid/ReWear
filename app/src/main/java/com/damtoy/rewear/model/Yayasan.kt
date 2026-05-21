package com.damtoy.rewear.model


data class Yayasan(
    val id: String,
    val name: String,
    val city: String,
    val address: String,
    val phone: String,
    val waLink: String,
    val acceptedCategories: List<ClothingCategory>,
    val logoRes: Int? = null // For local drawable resources
)