package com.example.phantom.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "defensive_actions")
data class DefensiveAction(
    @PrimaryKey val id: String = java.util.UUID.randomUUID().toString(),
    val timestamp: Long = System.currentTimeMillis(),
    val actionType: String, // delay_launch, hide_app, blur_content, disable_clipboard, mask_notification
    val targetApp: String = "",
    val triggeredByTrustScore: Int = 0,
    val durationMs: Long = 0L,
    val isActive: Boolean = true
)