package com.example.phantom.domain.repository

import com.example.phantom.domain.model.UserBehaviorSnapshot
import com.example.phantom.domain.model.TrustScore
import com.example.phantom.domain.model.TrustScoreHistory
import kotlinx.coroutines.flow.Flow

interface BehaviorRepository {
    suspend fun saveBehaviorSnapshot(snapshot: UserBehaviorSnapshot)
    suspend fun getRecentBehaviorSnapshots(limit: Int): List<UserBehaviorSnapshot>
    suspend fun getBehaviorSnapshotsInRange(fromTime: Long, toTime: Long): List<UserBehaviorSnapshot>
    fun getAllBehaviorSnapshots(): Flow<List<UserBehaviorSnapshot>>
    suspend fun deleteOldBehaviorSnapshots(olderThan: Long)
    suspend fun calculateTrustScore(): TrustScore
    suspend fun getTrustScoreHistory(): TrustScoreHistory
}