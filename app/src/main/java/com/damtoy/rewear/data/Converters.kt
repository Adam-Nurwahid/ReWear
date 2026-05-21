package com.damtoy.rewear.data

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.TypeConverter
import com.damtoy.rewear.model.ClothingCategory
import com.damtoy.rewear.model.FabricType
import java.time.LocalDate

class Converters {
    @TypeConverter fun fromFabricType(value: FabricType): String = value.name
    @TypeConverter fun toFabricType(value: String): FabricType = enumValueOf(value)
    @TypeConverter fun fromClothingCategory(value: ClothingCategory): String = value.name
    @TypeConverter fun toClothingCategory(value: String): ClothingCategory = enumValueOf(value)
    @RequiresApi(Build.VERSION_CODES.O)
    @TypeConverter fun fromLocalDate(date: LocalDate): Long = date.toEpochDay()
    @RequiresApi(Build.VERSION_CODES.O)
    @TypeConverter fun toLocalDate(epochDay: Long): LocalDate = LocalDate.ofEpochDay(epochDay)
}