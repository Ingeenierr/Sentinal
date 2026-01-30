package com.example.phantom.domain.repository

import com.example.phantom.domain.model.DefensiveAction
import kotlinx.coroutines.flow.Flow

interface DefensiveActionRepository {
    suspend fun logDefensiveAction(action: DefensiveAction)
    suspend fun getRecentDefensiveActions(limit: Int): List<DefensiveAction>
    fun getAllDefensiveActions(): Flow<List<DefensiveAction>>
    suspend fun getDefensiveActionsInRange(fromTime: Long, toTime: Long): List<DefensiveAction>
    suspend fun deleteOldDefensiveActions(olderThan: Long)
}