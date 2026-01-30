package com.example.phantom.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trust_scores")
data class TrustScore(
    @PrimaryKey val id: String = java.util.UUID.randomUUID().toString(),
    val timestamp: Long = System.currentTimeMillis(),
    val score: Int = 0, // 0-100
    val reason: String = "",
    val triggeredDefensiveAction: Boolean = false,
    val defensiveActionType: String = ""
)