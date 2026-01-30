package com.example.phantom.data.database

import androidx.room.*
import com.example.phantom.data.model.BehavioralPattern
import com.example.phantom.data.model.DefensiveAction
import com.example.phantom.data.model.TrustScore
import kotlinx.coroutines.flow.Flow

@Dao
interface BehavioralDao {
    // Behavioral Pattern methods
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBehavioralPattern(pattern: BehavioralPattern)
    
    @Query("SELECT * FROM behavioral_patterns ORDER BY timestamp DESC LIMIT 50")
    fun getAllBehavioralPatterns(): Flow<List<BehavioralPattern>>
    
    @Query("SELECT * FROM behavioral_patterns WHERE timestamp >= :fromTime ORDER BY timestamp DESC")
    fun getPatternsSince(fromTime: Long): Flow<List<BehavioralPattern>>
    
    @Query("DELETE FROM behavioral_patterns WHERE timestamp < :olderThan")
    suspend fun deleteOldPatterns(olderThan: Long)
    
    // Trust Score methods
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrustScore(score: TrustScore)
    
    @Query("SELECT * FROM trust_scores ORDER BY timestamp DESC LIMIT 50")
    fun getAllTrustScores(): Flow<List<TrustScore>>
    
    @Query("SELECT * FROM trust_scores WHERE timestamp >= :fromTime ORDER BY timestamp DESC")
    fun getScoresSince(fromTime: Long): Flow<List<TrustScore>>
    
    @Query("DELETE FROM trust_scores WHERE timestamp < :olderThan")
    suspend fun deleteOldScores(olderThan: Long)
    
    // Defensive Action methods
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDefensiveAction(action: DefensiveAction)
    
    @Query("SELECT * FROM defensive_actions ORDER BY timestamp DESC LIMIT 50")
    fun getAllDefensiveActions(): Flow<List<DefensiveAction>>
    
    @Query("SELECT * FROM defensive_actions WHERE timestamp >= :fromTime ORDER BY timestamp DESC")
    fun getActionsSince(fromTime: Long): Flow<List<DefensiveAction>>
    
    @Query("UPDATE defensive_actions SET isActive = 0 WHERE timestamp < :olderThan AND isActive = 1")
    suspend fun deactivateOldActions(olderThan: Long)
}