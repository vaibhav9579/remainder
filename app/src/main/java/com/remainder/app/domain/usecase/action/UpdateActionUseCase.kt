package com.remainder.app.domain.usecase.action

import com.remainder.app.domain.model.Action
import com.remainder.app.domain.repository.ActionRepository
import java.time.Instant
import javax.inject.Inject

class UpdateActionUseCase @Inject constructor(
    private val actionRepository: ActionRepository,
) {
    suspend operator fun invoke(action: Action) {
        actionRepository.updateAction(action.copy(updatedAt = Instant.now()))
    }
}
