package com.remainder.app.domain.usecase.action

import com.remainder.app.domain.model.Action
import com.remainder.app.domain.repository.ActionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetActionByIdUseCase @Inject constructor(
    private val actionRepository: ActionRepository,
) {
    operator fun invoke(id: Long): Flow<Action?> = actionRepository.getActionById(id)
}
