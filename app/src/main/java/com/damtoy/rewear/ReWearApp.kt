package com.damtoy.rewear

import android.app.Application
import com.damtoy.rewear.data.AppDatabase
import com.damtoy.rewear.repository.ClothingRepository
import com.google.firebase.FirebaseApp
import kotlin.getValue
import com.damtoy.rewear.repository.YayasanRepository
import com.damtoy.rewear.repository.UserRepository
class ReWearApp : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }

    val repository by lazy { ClothingRepository(database.clothingDao()) }

    val userRepository by lazy { UserRepository() }
    val yayasanRepository by lazy { YayasanRepository() }

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}