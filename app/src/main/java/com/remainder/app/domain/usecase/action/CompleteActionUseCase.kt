package com.remainder.app.domain.usecase.action

import com.remainder.app.domain.model.Action
import com.remainder.app.domain.repository.ActionRepository
import java.time.Instant
import javax.inject.Inject

class CompleteActionUseCase @Inject constructor(
    private val actionRepository: ActionRepository,
) {
    suspend operator fun invoke(action: Action) {
        val now = Instant.now()
        actionRepository.updateAction(
            action.copy(isCompleted = true, completedAt = now, updatedAt = now),
        )
    }
}
