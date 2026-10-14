package com.example.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class Tournament(
    val id: String = "",
    val title: String = "",
    val description: String? = null,
    val rules: String? = null,
    val gameTitle: String = "Free Fire MAX",
    val format: String = "Solo", // Solo, Duo, Squad
    val map: String = "Bermuda", // Bermuda, Purgatory, Kalahari, Alpine, NexTerra
    val maxSlots: Int = 48,
    val filledSlots: Int = 0,
    val entryFee: Int = 0,
    val prizePool: Int = 0,
    val firstPrize: Int = 0,
    val secondPrize: Int = 0,
    val thirdPrize: Int = 0,
    val perKillPrize: Int = 0,
    val status: String = "upcoming", // upcoming, live, completed, cancelled
    val roomReleased: Boolean = false,
    val winnerName: String? = null,
    val scheduledAt: Timestamp? = null,
    val registrationEnd: Timestamp? = null,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)
