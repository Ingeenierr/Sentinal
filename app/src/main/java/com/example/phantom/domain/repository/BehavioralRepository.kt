package com.example.phantom.domain.repository

import com.example.phantom.data.model.BehavioralPattern
import com.example.phantom.data.model.DefensiveAction
import com.example.phantom.data.model.TrustScore
import kotlinx.coroutines.flow.Flow

interface BehavioralRepository {
    // Behavioral Patterns
    suspend fun saveBehavioralPattern(pattern: BehavioralPattern)
    fun getRecentPatterns(limit: Int = 50): Flow<List<BehavioralPattern>>
    fun getPatternsSince(timestamp: Long): Flow<List<BehavioralPattern>>
    suspend fun clearOldPatterns(olderThan: Long)
    
    // Trust Scores
    suspend fun saveTrustScore(score: TrustScore)
    fun getRecentTrustScores(limit: Int = 50): Flow<List<TrustScore>>
    fun getScoresSince(timestamp: Long): Flow<List<TrustScore>>
    suspend fun clearOldScores(olderThan: Long)
    
    // Defensive Actions
    suspend fun saveDefensiveAction(action: DefensiveAction)
    fun getRecentDefensiveActions(limit: Int = 50): Flow<List<DefensiveAction>>
    fun getActionsSince(timestamp: Long): Flow<List<DefensiveAction>>
    suspend fun deactivateOldActions(olderThan: Long)
}