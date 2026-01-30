package com.example.phantom.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "behavior_snapshots")
data class UserBehaviorSnapshot(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val timestamp: Long = System.currentTimeMillis(),
    val touchCadence: Float = 0f, // Average time between touches in ms
    val scrollVelocity: Float = 0f, // Pixels per second
    val gestureSmoothness: Float = 0f, // Jerkiness metric (0-1)
    val appSwitchFrequency: Float = 0f, // Number of switches per minute
    val deviceOrientation: Int = 0, // 0=portrait, 1=landscape, 2=reversed portrait, 3=reversed landscape
    val usageTimingPattern: String = "", // JSON string representing hourly usage pattern
    val screenOnDuration: Long = 0L, // Duration in milliseconds
    val avgTouchPressure: Float = 0f, // Average pressure (0-1)
    val swipeAccuracy: Float = 0f, // Accuracy of swipes (0-1)
    val typingRhythm: Float = 0f, // Consistency of typing rhythm
    val trustScore: Int = 0 // Calculated trust score (0-100)
)