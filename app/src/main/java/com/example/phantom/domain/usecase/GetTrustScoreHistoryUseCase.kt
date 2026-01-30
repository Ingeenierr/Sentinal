package com.example.phantom.domain.usecase

import com.example.phantom.domain.model.TrustScoreHistory
import com.example.phantom.domain.repository.BehaviorRepository
import javax.inject.Inject

class GetTrustScoreHistoryUseCase @Inject constructor(
    private val behaviorRepository: BehaviorRepository
) {
    suspend operator fun invoke(): TrustScoreHistory {
        return behaviorRepository.getTrustScoreHistory()
    }
}