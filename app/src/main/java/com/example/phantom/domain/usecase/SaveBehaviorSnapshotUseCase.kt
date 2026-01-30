package com.example.phantom.domain.usecase

import com.example.phantom.domain.model.UserBehaviorSnapshot
import com.example.phantom.domain.repository.BehaviorRepository
import javax.inject.Inject

class SaveBehaviorSnapshotUseCase @Inject constructor(
    private val behaviorRepository: BehaviorRepository
) {
    suspend operator fun invoke(snapshot: UserBehaviorSnapshot) {
        behaviorRepository.saveBehaviorSnapshot(snapshot)
    }
}