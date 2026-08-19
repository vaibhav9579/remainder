package com.remainder.app.domain.usecase.action

import com.remainder.app.domain.model.Action
import com.remainder.app.domain.repository.ActionRepository
import java.time.Instant
import javax.inject.Inject

class AddActionUseCase @Inject constructor(
    private val actionRepository: ActionRepository,
) {
    suspend operator fun invoke(action: Action): Long {
        val now = Instant.now()
        return actionRepository.addAction(action.copy(createdAt = now, updatedAt = now))
    }
}
