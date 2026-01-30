package com.example.phantom.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant

@Entity(tableName = "behavior_snapshots")
data class BehaviorSnapshot(
    @PrimaryKey val id: String = java.util.UUID.randomUUID().toString(),
    val timestamp: Long = System.currentTimeMillis(),
    val touchCadence: Float,           // Average time between touches (ms)
    val scrollVelocity: Float,         // Average scrolling speed (dp/s)
    val gestureSmoothness: Float,      // Smoothness of gestures (0-1)
    val appSwitchFrequency: Int,       // Number of app switches per minute
    val deviceOrientation: Int,        // 0=portrait, 1=landscape, 2=reversed portrait, 3=reversed landscape
    val usageTimeOfDay: Int,          // Hour of day when activity occurred (0-23)
    val sessionDuration: Long,        // Duration of the session in milliseconds
    val screenOnCount: Int,           // Number of times screen was turned on
    val notificationInteraction: Int,  // Number of notification interactions
    val trustScore: Int              // Calculated trust score (0-100)
)