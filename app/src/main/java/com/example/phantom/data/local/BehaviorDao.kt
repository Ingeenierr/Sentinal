package com.example.phantom.data.local

import androidx.room.*
import com.example.phantom.domain.model.UserBehaviorSnapshot
import com.example.phantom.domain.model.DefensiveAction
import kotlinx.coroutines.flow.Flow

@Dao
interface BehaviorDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBehaviorSnapshot(snapshot: UserBehaviorSnapshot)

    @Query("SELECT * FROM behavior_snapshots ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getRecentBehaviorSnapshots(limit: Int): List<UserBehaviorSnapshot>

    @Query("SELECT * FROM behavior_snapshots WHERE timestamp >= :fromTime AND timestamp <= :toTime ORDER BY timestamp ASC")
    suspend fun getBehaviorSnapshotsInRange(fromTime: Long, toTime: Long): List<UserBehaviorSnapshot>

    @Query("SELECT * FROM behavior_snapshots ORDER BY timestamp DESC")
    fun getAllBehaviorSnapshots(): Flow<List<UserBehaviorSnapshot>>

    @Query("DELETE FROM behavior_snapshots WHERE timestamp < :olderThan")
    suspend fun deleteOldBehaviorSnapshots(olderThan: Long)

    @Query("DELETE FROM behavior_snapshots")
    suspend fun clearAll()
}