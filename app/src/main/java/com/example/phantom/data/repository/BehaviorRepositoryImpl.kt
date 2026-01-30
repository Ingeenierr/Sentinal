package com.example.phantom.data.repository

import com.example.phantom.domain.model.UserBehaviorSnapshot
import com.example.phantom.domain.model.TrustScore
import com.example.phantom.domain.model.RiskLevel
import com.example.phantom.domain.model.TrustScoreHistory
import com.example.phantom.domain.repository.BehaviorRepository
import com.example.phantom.data.local.BehaviorDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class BehaviorRepositoryImpl @Inject constructor(
    private val behaviorDao: BehaviorDao
) : BehaviorRepository {
    
    override suspend fun saveBehaviorSnapshot(snapshot: UserBehaviorSnapshot) {
        behaviorDao.insertBehaviorSnapshot(snapshot)
    }
    
    override suspend fun getRecentBehaviorSnapshots(limit: Int): List<UserBehaviorSnapshot> {
        return behaviorDao.getRecentBehaviorSnapshots(limit)
    }
    
    override suspend fun getBehaviorSnapshotsInRange(fromTime: Long, toTime: Long): List<UserBehaviorSnapshot> {
        return behaviorDao.getBehaviorSnapshotsInRange(fromTime, toTime)
    }
    
    override fun getAllBehaviorSnapshots(): Flow<List<UserBehaviorSnapshot>> {
        return behaviorDao.getAllBehaviorSnapshots()
    }
    
    override suspend fun deleteOldBehaviorSnapshots(olderThan: Long) {
        behaviorDao.deleteOldBehaviorSnapshots(olderThan)
    }
    
    override suspend fun calculateTrustScore(): TrustScore {
        // Get recent behavior snapshots to analyze
        val recentSnapshots = getRecentBehaviorSnapshots(50) // Last 50 snapshots
        
        if (recentSnapshots.isEmpty()) {
            // If no historical data, return neutral score
            return TrustScore(
                score = 75, // Start with a reasonably high score
                confidence = 0.5f,
                anomalyDetected = false,
                riskLevel = RiskLevel.LOW
            )
        }
        
        // Calculate statistical measures based on historical data
        val avgTouchCadence = recentSnapshots.map { it.touchCadence }.average().toFloat()
        val avgScrollVelocity = recentSnapshots.map { it.scrollVelocity }.average().toFloat()
        val avgGestureSmoothness = recentSnapshots.map { it.gestureSmoothness }.average().toFloat()
        val avgAppSwitchFreq = recentSnapshots.map { it.appSwitchFrequency }.average().toFloat()
        val avgTouchPressure = recentSnapshots.map { it.avgTouchPressure }.average().toFloat()
        val avgSwipeAccuracy = recentSnapshots.map { it.swipeAccuracy }.average().toFloat()
        val avgTypingRhythm = recentSnapshots.map { it.typingRhythm }.average().toFloat()
        
        // Calculate standard deviations for each metric
        val touchCadenceStdDev = calculateStandardDeviation(recentSnapshots.map { it.touchCadence }, avgTouchCadence.toDouble())
        val scrollVelocityStdDev = calculateStandardDeviation(recentSnapshots.map { it.scrollVelocity }, avgScrollVelocity.toDouble())
        val gestureSmoothnessStdDev = calculateStandardDeviation(recentSnapshots.map { it.gestureSmoothness }, avgGestureSmoothness.toDouble())
        val appSwitchFreqStdDev = calculateStandardDeviation(recentSnapshots.map { it.appSwitchFrequency }, avgAppSwitchFreq.toDouble())
        val touchPressureStdDev = calculateStandardDeviation(recentSnapshots.map { it.avgTouchPressure }, avgTouchPressure.toDouble())
        val swipeAccuracyStdDev = calculateStandardDeviation(recentSnapshots.map { it.swipeAccuracy }, avgSwipeAccuracy.toDouble())
        val typingRhythmStdDev = calculateStandardDeviation(recentSnapshots.map { it.typingRhythm }, avgTypingRhythm.toDouble())
        
        // Get the latest snapshot to compare against historical averages
        val latestSnapshot = recentSnapshots.first()
        
        // Calculate z-scores for each metric (how many standard deviations away from mean)
        val touchCadenceZScore = if (touchCadenceStdDev == 0.0) 0.0 else 
            Math.abs(latestSnapshot.touchCadence - avgTouchCadence) / touchCadenceStdDev
        val scrollVelocityZScore = if (scrollVelocityStdDev == 0.0) 0.0 else 
            Math.abs(latestSnapshot.scrollVelocity - avgScrollVelocity) / scrollVelocityStdDev
        val gestureSmoothnessZScore = if (gestureSmoothnessStdDev == 0.0) 0.0 else 
            Math.abs(latestSnapshot.gestureSmoothness - avgGestureSmoothness) / gestureSmoothnessStdDev
        val appSwitchFreqZScore = if (appSwitchFreqStdDev == 0.0) 0.0 else 
            Math.abs(latestSnapshot.appSwitchFrequency - avgAppSwitchFreq) / appSwitchFreqStdDev
        val touchPressureZScore = if (touchPressureStdDev == 0.0) 0.0 else 
            Math.abs(latestSnapshot.avgTouchPressure - avgTouchPressure) / touchPressureStdDev
        val swipeAccuracyZScore = if (swipeAccuracyStdDev == 0.0) 0.0 else 
            Math.abs(latestSnapshot.swipeAccuracy - avgSwipeAccuracy) / swipeAccuracyStdDev
        val typingRhythmZScore = if (typingRhythmStdDev == 0.0) 0.0 else 
            Math.abs(latestSnapshot.typingRhythm - avgTypingRhythm) / typingRhythmStdDev
        
        // Weight each metric differently (some are more important than others)
        val weightedScore = (
            touchCadenceZScore * 0.15 +
            scrollVelocityZScore * 0.10 +
            gestureSmoothnessZScore * 0.20 +
            appSwitchFreqZScore * 0.15 +
            touchPressureZScore * 0.10 +
            swipeAccuracyZScore * 0.15 +
            typingRhythmZScore * 0.15
        )
        
        // Convert to trust score (0-100, inverse relationship - higher z-score = lower trust)
        var trustScore = (100 - (weightedScore * 20)).toInt().coerceIn(0, 100)
        
        // Adjust based on confidence in the data
        val confidence = calculateConfidence(recentSnapshots.size, touchCadenceStdDev, gestureSmoothnessStdDev)
        
        // Determine if anomaly detected (z-score > 2 means significantly different from baseline)
        val anomalyDetected = weightedScore > 2.0
        val anomalyType = if (anomalyDetected) detectAnomalyType(latestSnapshot, recentSnapshots) else ""
        
        // Determine risk level based on trust score
        val riskLevel = when {
            trustScore >= 70 -> RiskLevel.LOW
            trustScore >= 40 -> RiskLevel.MEDIUM
            trustScore >= 20 -> RiskLevel.HIGH
            else -> RiskLevel.CRITICAL
        }
        
        return TrustScore(
            score = trustScore,
            confidence = confidence.toFloat(),
            anomalyDetected = anomalyDetected,
            anomalyType = anomalyType,
            riskLevel = riskLevel
        )
    }
    
    private fun calculateStandardDeviation(values: List<Float>, mean: Double): Double {
        if (values.size < 2) return 0.0
        
        val variance = values.sumOf { Math.pow(it.toDouble() - mean, 2.0) } / (values.size - 1)
        return Math.sqrt(variance)
    }
    
    private fun calculateConfidence(snapshotCount: Int, touchCadenceStdDev: Double, gestureSmoothnessStdDev: Double): Double {
        // Higher confidence with more data points and consistent behavior
        val sampleSizeFactor = Math.min(snapshotCount / 50.0, 1.0) // Max confidence at 50+ samples
        val consistencyFactor = 1.0 / (1.0 + touchCadenceStdDev + gestureSmoothnessStdDev) // More consistent = higher confidence
        
        return (sampleSizeFactor * 0.6 + consistencyFactor * 0.4).coerceIn(0.1, 1.0)
    }
    
    private fun detectAnomalyType(latest: UserBehaviorSnapshot, historical: List<UserBehaviorSnapshot>): String {
        val recentAvg = historical.take(10) // Look at most recent 10 samples
        
        val metrics = mutableListOf<String>()
        
        if (Math.abs(latest.touchCadence - recentAvg.map { it.touchCadence }.average().toFloat()) / 
            calculateStandardDeviation(historical.map { it.touchCadence }, historical.map { it.touchCadence }.average()) > 2.0) {
            metrics.add("TOUCH_CADENCE")
        }
        
        if (Math.abs(latest.gestureSmoothness - recentAvg.map { it.gestureSmoothness }.average().toFloat()) / 
            calculateStandardDeviation(historical.map { it.gestureSmoothness }, historical.map { it.gestureSmoothness }.average()) > 2.0) {
            metrics.add("GESTURE_SMOOTHNESS")
        }
        
        if (Math.abs(latest.appSwitchFrequency - recentAvg.map { it.appSwitchFrequency }.average().toFloat()) / 
            calculateStandardDeviation(historical.map { it.appSwitchFrequency }, historical.map { it.appSwitchFrequency }.average()) > 2.0) {
            metrics.add("APP_SWITCH_FREQUENCY")
        }
        
        if (metrics.isEmpty()) return "GENERAL_BEHAVIOR_PATTERN"
        return metrics.joinToString(",")
    }
    
    override suspend fun getTrustScoreHistory(): TrustScoreHistory {
        val snapshots = getBehaviorSnapshotsInRange(
            System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000), // Last 7 days
            System.currentTimeMillis()
        )
        
        val scores = snapshots.map { 
            TrustScore(
                score = it.trustScore,
                confidence = 0.8f, // Placeholder confidence
                timestamp = it.timestamp
            )
        }
        
        val averageScore = if (scores.isNotEmpty()) {
            scores.sumOf { it.score.toDouble() } / scores.size
        } else 0.0
        
        // Calculate trend (simple linear regression approximation)
        var trend = 0.0
        if (scores.size > 1) {
            val firstHalfAvg = scores.take(scores.size / 2).sumOf { it.score.toDouble() } / (scores.size / 2)
            val secondHalfAvg = scores.drop(scores.size / 2).sumOf { it.score.toDouble() } / scores.drop(scores.size / 2).size
            trend = (secondHalfAvg - firstHalfAvg) / 100.0 // Normalize to -1 to 1 range
        }
        
        return TrustScoreHistory(
            scores = scores,
            averageScore = averageScore.toFloat(),
            trend = trend.toFloat()
        )
    }
}