package com.damtoy.rewear.domain


import com.damtoy.rewear.model.ClothingItem
import com.damtoy.rewear.model.FabricCategory


data class WardrobeSummary(
    val totalItems: Int,
    val totalCo2: Double,
    val totalWater: Double,
    val ecoScore: Int = 85
)

object CarbonCalculator {

    /**
     * Calculates the overall environmental footprint of the user's specific wardrobe.
     */
    fun calculateWardrobeSummary(items: List<ClothingItem>): WardrobeSummary {
        val totalItems = items.size
        val totalCo2 = items.sumOf { it.totalCo2Impact }
        val totalWater = items.sumOf { it.totalWaterImpact }

        return WardrobeSummary(totalItems, totalCo2, totalWater)
    }

    /**
     * Generates dynamic, data-driven tips based on the user's specific wardrobe contents.
     */
    fun generateTips(items: List<ClothingItem>): List<String> {
        val tips = mutableListOf<String>()

        if (items.isEmpty()) {
            tips.add("Welcome to ReWear! Add your first item to see your environmental footprint.")
            return tips
        }

        // Rule 1: Natural fabrics with high water footprint
        val highWaterItems = items.count { it.fabricType.category == FabricCategory.NATURAL }
        if (highWaterItems > 3) {
            tips.add("You have several natural fabric items (like Cotton). Try spot-cleaning them instead of doing full washes to save massive amounts of water.")
        }

        // Rule 2: Synthetic fabrics (Microplastics)
        val syntheticItems = items.count { it.fabricType.category == FabricCategory.SYNTHETIC }
        if (syntheticItems > 3) {
            tips.add("Your synthetic clothes shed microplastics in the laundry. Consider washing them less frequently or using a microplastic-catching bag.")
        }

        // Rule 3: Wear frequency
        val totalWears = items.sumOf { it.timesWorn }
        val avgWears = totalWears.toDouble() / items.size
        if (avgWears < 5) {
            tips.add("Try the #30Wears challenge! Aim to wear each item at least 30 times to maximize its lifecycle.")
        } else {
            tips.add("Great job re-wearing your clothes! Extending the life of your garments significantly reduces global fashion waste.")
        }

        return tips.shuffled().take(3)
    }
}