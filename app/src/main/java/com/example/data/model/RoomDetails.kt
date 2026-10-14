package com.example.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class RoomDetails(
    @DocumentId val id: String = "details",
    val roomId: String = "",
    val roomPassword: String = "",
    val instructions: String = "Join 10 minutes before match time. Emulators are strictly prohibited unless tournament format specifies.",
    @ServerTimestamp val updatedAt: Timestamp? = null
)
