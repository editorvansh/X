package com.example.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class Announcement(
    val id: String = "",
    val title: String = "",
    val content: String = "",
    val tag: String = "Update",
    @ServerTimestamp val createdAt: Timestamp? = null
)
