package com.example.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class LeaderboardEntry(
    val id: String = "",
    val userId: String? = null,
    val playerName: String = "",
    val inGameId: String = "",
    val matchesPlayed: Int = 0,
    val kills: Int = 0,
    val wins: Int = 0,
    val totalPoints: Int = 0,
    val rank: Int = 1,
    @ServerTimestamp val updatedAt: Timestamp? = null
)
