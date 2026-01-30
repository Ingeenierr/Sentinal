package com.example.phantom.data.repository

import com.example.phantom.domain.model.DefensiveAction
import com.example.phantom.domain.repository.DefensiveActionRepository
import com.example.phantom.data.local.DefensiveActionDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DefensiveActionRepositoryImpl @Inject constructor(
    private val defensiveActionDao: DefensiveActionDao
) : DefensiveActionRepository {
    
    override suspend fun logDefensiveAction(action: DefensiveAction) {
        defensiveActionDao.insertDefensiveAction(action)
    }
    
    override suspend fun getRecentDefensiveActions(limit: Int): List<DefensiveAction> {
        return defensiveActionDao.getRecentDefensiveActions(limit)
    }
    
    override fun getAllDefensiveActions(): Flow<List<DefensiveAction>> {
        return defensiveActionDao.getAllDefensiveActions()
    }
    
    override suspend fun getDefensiveActionsInRange(fromTime: Long, toTime: Long): List<DefensiveAction> {
        return defensiveActionDao.getDefensiveActionsInRange(fromTime, toTime)
    }
    
    override suspend fun deleteOldDefensiveActions(olderThan: Long) {
        defensiveActionDao.deleteOldDefensiveActions(olderThan)
    }
}