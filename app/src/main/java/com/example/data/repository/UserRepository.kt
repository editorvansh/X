package com.example.data.repository

import android.content.Context
import com.example.R
import com.example.data.model.UserProfile
import com.example.util.OperationType
import com.example.util.handleFirestoreError
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class UserRepository(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth = Firebase.auth
) {
    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        ),
        Firebase.auth
    )

    private fun requireUserId(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("User must be signed in with Google before accessing Firestore.")
    }

    fun observeUserProfile(userId: String): Flow<UserProfile?> = flow {
        val path = "users/$userId"
        emitAll(
            db.collection("users").document(userId)
                .snapshots()
                .map { snapshot -> snapshot.toObject(UserProfile::class.java) }
                .catch { error ->
                    if (error is Exception) handleFirestoreError(error, OperationType.GET, path)
                    throw error
                }
        )
    }

    suspend fun getProfile(userId: String): Result<UserProfile?> = try {
        val path = "users/$userId"
        val doc = db.collection("users").document(userId).get().await()
        Result.success(doc.toObject(UserProfile::class.java))
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        handleFirestoreError(e, OperationType.GET, "users/$userId")
        Result.failure(e)
    }

    suspend fun checkAdmin(userId: String, email: String?): Boolean {
        if (email?.equals("saritakumari9818422028@gmail.com", ignoreCase = true) == true) return true
        if (email?.equals("opgandhujf@gmaol.com", ignoreCase = true) == true) return true
        if (email?.contains("admin", ignoreCase = true) == true) return true
        if (email?.endsWith("@akshartournament.com", ignoreCase = true) == true) return true
        return try {
            val userDoc = db.collection("users").document(userId).get().await()
            if (userDoc.getString("role") == "admin") return true
            val doc = db.collection("admins").document(userId).get().await()
            doc.exists()
        } catch (e: Exception) {
            false
        }
    }

    suspend fun createOrUpdateProfileFromAuth(user: FirebaseUser): Result<Unit> {
        return createOrUpdateInitialProfile(
            displayName = user.displayName ?: "Player",
            email = user.email ?: "",
            photoUrl = user.photoUrl?.toString()
        )
    }

    suspend fun createOrUpdateInitialProfile(
        displayName: String,
        email: String,
        photoUrl: String?
    ): Result<Unit> = try {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid)
        val existingDoc = docRef.get().await()
        
        val isAdminUser = email.equals("saritakumari9818422028@gmail.com", ignoreCase = true) ||
                email.equals("opgandhujf@gmaol.com", ignoreCase = true) ||
                email.contains("admin", ignoreCase = true) ||
                email.endsWith("@akshartournament.com", ignoreCase = true)

        if (!existingDoc.exists()) {
            val payload = mutableMapOf<String, Any>(
                "userId" to uid,
                "email" to email,
                "displayName" to displayName,
                "role" to if (isAdminUser) "admin" else "user",
                "totalMatches" to 0,
                "totalKills" to 0,
                "totalWins" to 0,
                "totalPoints" to 0,
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )
            photoUrl?.let { payload["photoUrl"] = it }
            docRef.set(payload, SetOptions.merge()).await()
        } else {
            val updatePayload = mutableMapOf<String, Any>(
                "updatedAt" to FieldValue.serverTimestamp()
            )
            if (displayName.isNotBlank() && displayName != "Player") {
                updatePayload["displayName"] = displayName
            }
            if (isAdminUser && existingDoc.getString("role") != "admin") {
                updatePayload["role"] = "admin"
            }
            photoUrl?.let { updatePayload["photoUrl"] = it }
            docRef.set(updatePayload, SetOptions.merge()).await()
        }
        Result.success(Unit)
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        handleFirestoreError(e, OperationType.WRITE, "users/${auth.currentUser?.uid}")
        Result.failure(e)
    }

    suspend fun updateGamerProfile(userId: String, inGameName: String, inGameId: String): Result<Unit> {
        return updateInGameCredentials(inGameId, inGameName)
    }

    suspend fun updateInGameCredentials(inGameId: String, inGameName: String): Result<Unit> = try {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid)
        val payload = mapOf(
            "inGameId" to inGameId.trim(),
            "inGameName" to inGameName.trim(),
            "updatedAt" to FieldValue.serverTimestamp()
        )
        docRef.update(payload).await()
        Result.success(Unit)
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        handleFirestoreError(e, OperationType.UPDATE, "users/${auth.currentUser?.uid}")
        Result.failure(e)
    }

    suspend fun deleteAccount(): Result<Unit> = try {
        val uid = requireUserId()
        db.collection("users").document(uid).delete().await()
        auth.currentUser?.delete()?.await()
        Result.success(Unit)
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        handleFirestoreError(e, OperationType.DELETE, "users/${auth.currentUser?.uid}")
        Result.failure(e)
    }
}
