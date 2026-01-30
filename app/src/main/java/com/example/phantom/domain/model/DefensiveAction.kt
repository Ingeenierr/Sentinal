package com.example.phantom.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "defensive_actions")
data class DefensiveAction(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val timestamp: Long = System.currentTimeMillis(),
    val actionType: DefensiveActionType,
    val triggeredByTrustScore: Int,
    val durationMs: Long = 0L, // How long the action was active
    val targetApps: List<String> = emptyList(), // Apps affected by the action
    val description: String = ""
)

enum class DefensiveActionType {
    LAUNCH_DELAY,
    APP_HIDE,
    CONTENT_BLUR,
    CLIPBOARD_DISABLE,
    NOTIFICATION_MASK,
    SCREEN_LOCK_DELAY
}