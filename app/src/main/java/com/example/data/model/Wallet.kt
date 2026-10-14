package com.example.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class Wallet(
    val userId: String = "",
    val coins: Int = 100, // Initial 100 Welcome Coins for new players
    val totalAdded: Int = 100,
    val totalSpent: Int = 0,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
) {
    val referenceValueInr: Int get() = coins
}
