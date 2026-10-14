package com.example.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class WalletTransaction(
    val id: String = "",
    val userId: String = "",
    val type: String = "MATCH_ENTRY", // WELCOME_BONUS, TEST_TOPUP, MATCH_ENTRY, REFUND, PRIZE_PAYOUT
    val amount: Int = 0,
    val referenceId: String? = null,
    val referenceTitle: String? = null,
    val status: String = "COMPLETED", // COMPLETED, PENDING, FAILED
    val idempotencyKey: String = "",
    val description: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null
) {
    val isCredit: Boolean get() = amount > 0
    val referenceValueInr: Int get() = kotlin.math.abs(amount)
}
