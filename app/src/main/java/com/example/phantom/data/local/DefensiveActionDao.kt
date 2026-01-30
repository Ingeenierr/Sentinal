package com.example.phantom.data.local

import androidx.room.*
import com.example.phantom.domain.model.DefensiveAction
import kotlinx.coroutines.flow.Flow

@Dao
interface DefensiveActionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDefensiveAction(action: DefensiveAction)

    @Query("SELECT * FROM defensive_actions ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getRecentDefensiveActions(limit: Int): List<DefensiveAction>

    @Query("SELECT * FROM defensive_actions ORDER BY timestamp DESC")
    fun getAllDefensiveActions(): Flow<List<DefensiveAction>>

    @Query("SELECT * FROM defensive_actions WHERE timestamp >= :fromTime AND timestamp <= :toTime ORDER BY timestamp DESC")
    suspend fun getDefensiveActionsInRange(fromTime: Long, toTime: Long): List<DefensiveAction>

    @Query("DELETE FROM defensive_actions WHERE timestamp < :olderThan")
    suspend fun deleteOldDefensiveActions(olderThan: Long)

    @Query("DELETE FROM defensive_actions")
    suspend fun clearAll()
}