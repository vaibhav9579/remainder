package com.remainder.app.domain.usecase.action

import com.remainder.app.domain.model.Action
import com.remainder.app.domain.repository.ActionRepository
import javax.inject.Inject

class DeleteActionUseCase @Inject constructor(
    private val actionRepository: ActionRepository,
) {
    suspend operator fun invoke(action: Action) {
        actionRepository.deleteAction(action)
    }
}
