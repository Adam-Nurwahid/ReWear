package com.damtoy.rewear.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "clothing_items")
data class ClothingItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: String, // IMPORTANT: Ties this item to a specific Firebase UID
    val name: String,
    val category: ClothingCategory,
    val fabricType: FabricType,
    val weightKg: Double,
    val imageUri: String,
    val timesWorn: Int = 0,
    val dateAdded: LocalDate,
    val isForSwap: Boolean = false,
    val isForDonation: Boolean = false, // New for v2
    val donationYayasanId: String? = null // Stores where it was donated
) {
    val totalCo2Impact: Double
        get() = fabricType.co2PerKg * weightKg

    val totalWaterImpact: Double
        get() = fabricType.waterPerKg * weightKg
}