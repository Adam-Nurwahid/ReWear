package com.damtoy.rewear.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.damtoy.rewear.model.ClothingItem
import kotlinx.coroutines.flow.Flow

@Dao
interface ClothingDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClothing(item: ClothingItem)

    @Update
    suspend fun updateClothing(item: ClothingItem)

    @Delete
    suspend fun deleteClothing(item: ClothingItem)

    // Flow automatically emits updates when the database changes
    @Query("SELECT * FROM clothing_items ORDER BY dateAdded DESC")
    fun getAllClothing(): Flow<List<ClothingItem>>

    @Query("SELECT * FROM clothing_items WHERE id = :itemId LIMIT 1")
    fun getClothingById(itemId: Int): Flow<ClothingItem>

    @Query("SELECT * FROM clothing_items WHERE category = :category")
    fun getClothingByCategory(category: String): Flow<List<ClothingItem>>

    @Query("SELECT * FROM clothing_items WHERE isForSwap = 1")
    fun getSwapClothing(): Flow<List<ClothingItem>>


    // Filtered by current user's Firebase UID
    @Query("SELECT * FROM clothing_items WHERE userId = :userId ORDER BY dateAdded DESC")
    fun getAllByUser(userId: String): Flow<List<ClothingItem>>

    @Query("SELECT * FROM clothing_items WHERE id = :itemId AND userId = :userId LIMIT 1")
    fun getClothingById(itemId: Int, userId: String): Flow<ClothingItem>

    // Separated Swap and Donate flows
    @Query("SELECT * FROM clothing_items WHERE userId = :userId AND isForSwap = 1")
    fun getSwapByUser(userId: String): Flow<List<ClothingItem>>

    @Query("SELECT * FROM clothing_items WHERE userId = :userId AND isForDonation = 1")
    fun getDonationByUser(userId: String): Flow<List<ClothingItem>>
}



