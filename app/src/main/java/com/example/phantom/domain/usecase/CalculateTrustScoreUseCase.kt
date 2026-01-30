package com.example.phantom.domain.usecase

import com.example.phantom.domain.model.TrustScore
import com.example.phantom.domain.repository.BehaviorRepository
import javax.inject.Inject

class CalculateTrustScoreUseCase @Inject constructor(
    private val behaviorRepository: BehaviorRepository
) {
    suspend operator fun invoke(): TrustScore {
        return behaviorRepository.calculateTrustScore()
    }
}