package com.example.phantom.domain.usecase

import com.example.phantom.domain.model.DefensiveAction
import com.example.phantom.domain.repository.DefensiveActionRepository
import javax.inject.Inject

class LogDefensiveActionUseCase @Inject constructor(
    private val defensiveActionRepository: DefensiveActionRepository
) {
    suspend operator fun invoke(action: DefensiveAction) {
        defensiveActionRepository.logDefensiveAction(action)
    }
}