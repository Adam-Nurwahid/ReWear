package com.damtoy.rewear.repository


import com.damtoy.rewear.data.ClothingDao
import com.damtoy.rewear.domain.CarbonCalculator
import com.damtoy.rewear.domain.WardrobeSummary
import com.damtoy.rewear.model.ClothingItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ClothingRepository(private val clothingDao: ClothingDao) {

    // 1. Data Streams filtered by Firebase UID
    fun getAllClothing(userId: String): Flow<List<ClothingItem>> = clothingDao.getAllByUser(userId)
    fun getSwapClothing(userId: String): Flow<List<ClothingItem>> = clothingDao.getSwapByUser(userId)
    fun getDonationClothing(userId: String): Flow<List<ClothingItem>> = clothingDao.getDonationByUser(userId)
    fun getClothingById(itemId: Int, userId: String): Flow<ClothingItem> = clothingDao.getClothingById(itemId, userId)

    // 2. Transformed Data Streams
    fun getWardrobeSummary(userId: String): Flow<WardrobeSummary> = getAllClothing(userId).map { items ->
        CarbonCalculator.calculateWardrobeSummary(items)
    }

    fun getEcoTips(userId: String): Flow<List<String>> = getAllClothing(userId).map { items ->
        CarbonCalculator.generateTips(items)
    }

    // 3. Database Operations
    suspend fun addClothing(item: ClothingItem) {
        clothingDao.insertClothing(item)
    }

    suspend fun updateClothing(item: ClothingItem) {
        clothingDao.updateClothing(item)
    }

    suspend fun deleteClothing(item: ClothingItem) {
        clothingDao.deleteClothing(item)
    }

    suspend fun incrementWearCount(item: ClothingItem) {
        val updatedItem = item.copy(timesWorn = item.timesWorn + 1)
        clothingDao.updateClothing(updatedItem)
    }

    suspend fun toggleSwapStatus(item: ClothingItem) {
        val updatedItem = item.copy(isForSwap = !item.isForSwap, isForDonation = false)
        clothingDao.updateClothing(updatedItem)
    }

    suspend fun markForDonation(item: ClothingItem) {
        val updatedItem = item.copy(isForDonation = true, isForSwap = false)
        clothingDao.updateClothing(updatedItem)
    }
}