package com.damtoy.rewear.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.damtoy.rewear.model.ClothingItem

@Database(entities = [ClothingItem::class], version = 2, exportSchema = false) // Bumped to version 2
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun clothingDao(): ClothingDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "rewear_database"
                )
                    .fallbackToDestructiveMigration() // Clears old v1 data without crashing
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}