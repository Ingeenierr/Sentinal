package com.example.phantom.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant

@Entity(tableName = "behavioral_patterns")
data class BehavioralPattern(
    @PrimaryKey val id: String = java.util.UUID.randomUUID().toString(),
    val timestamp: Long = System.currentTimeMillis(),
    val touchCadence: Double = 0.0,
    val scrollVelocity: Double = 0.0,
    val gestureSmoothness: Double = 0.0,
    val appSwitchFrequency: Double = 0.0,
    val deviceOrientation: Int = 0,
    val usageTiming: Long = 0L,
    val sessionDuration: Long = 0L,
    val patternType: String = "normal" // normal, anomaly, suspicious
)