package com.example.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class Registration(
    val id: String = "",
    val tournamentId: String = "",
    val userId: String = "",
    val playerDisplayName: String = "",
    val inGameId: String = "",
    val inGameName: String = "",
    val teamName: String? = null,
    val teammates: String? = null,
    val status: String = "confirmed",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)
