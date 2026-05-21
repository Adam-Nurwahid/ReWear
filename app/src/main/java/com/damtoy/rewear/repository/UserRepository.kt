package com.damtoy.rewear.repository


import com.damtoy.rewear.model.UserProfile
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class UserRepository {
    private val db = FirebaseFirestore.getInstance()
    private val usersCollection = db.collection("users")

    suspend fun createUserProfile(userProfile: UserProfile) {
        // Uses the Firebase UID as the document ID for easy retrieval
        usersCollection.document(userProfile.uid).set(userProfile).await()
    }

    suspend fun getUserProfile(uid: String): UserProfile? {
        val snapshot = usersCollection.document(uid).get().await()
        return snapshot.toObject(UserProfile::class.java)
    }

    fun getUserProfileFlow(uid: String): Flow<UserProfile?> = callbackFlow {
        if (uid.isEmpty()) {
            trySend(null)
            close()
            return@callbackFlow
        }
        val listener = usersCollection.document(uid).addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.exists()) {
                val profile = snapshot.toObject(UserProfile::class.java)
                trySend(profile)
            } else {
                trySend(null)
            }
        }
        awaitClose { listener.remove() }
    }

    suspend fun updateEcoScore(uid: String, newScore: Int) {
        usersCollection.document(uid).update("ecoScore", newScore).await()
    }

    suspend fun incrementImpact(uid: String, co2Saved: Double, scorePoints: Int, addedDonations: Int = 0, addedSwaps: Int = 0) {
        val profile = getUserProfile(uid) ?: return
        val newCo2 = profile.totalCo2Saved + co2Saved
        val newScore = profile.ecoScore + scorePoints
        val newDonations = profile.donationCount + addedDonations
        val newSwaps = profile.swapCount + addedSwaps
        usersCollection.document(uid).update(
            mapOf(
                "totalCo2Saved" to newCo2,
                "ecoScore" to newScore,
                "donationCount" to newDonations,
                "swapCount" to newSwaps
            )
        ).await()
    }
}