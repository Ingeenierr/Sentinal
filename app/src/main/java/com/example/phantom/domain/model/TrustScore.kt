package com.example.phantom.domain.model

data class TrustScore(
    val score: Int, // Current trust score (0-100)
    val confidence: Float, // Confidence level in the score (0-1)
    val timestamp: Long = System.currentTimeMillis(),
    val anomalyDetected: Boolean = false,
    val anomalyType: String = "", // Type of anomaly detected
    val riskLevel: RiskLevel = RiskLevel.LOW
)

enum class RiskLevel {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL
}

data class TrustScoreHistory(
    val scores: List<TrustScore>,
    val averageScore: Float,
    val trend: Float // Trend over time (-1.0 to 1.0)
)