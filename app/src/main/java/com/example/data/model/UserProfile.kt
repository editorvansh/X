package com.example.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class UserProfile(
    val id: String = "",
    val userId: String = "",
    val email: String = "",
    val displayName: String = "",
    val photoUrl: String? = null,
    val inGameId: String? = null,
    val inGameName: String? = null,
    val role: String = "user",
    val totalMatches: Int = 0,
    val totalKills: Int = 0,
    val totalWins: Int = 0,
    val totalPoints: Int = 0,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
) {
    val freeFireUid: String? get() = inGameId
    val matchesPlayed: Int get() = totalMatches
    val wins: Int get() = totalWins
    val kills: Int get() = totalKills
}
