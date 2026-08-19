package com.remainder.app.domain.usecase.action

import com.remainder.app.domain.model.Action
import com.remainder.app.domain.repository.ActionRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject

class GetActionsForDateUseCase @Inject constructor(
    private val actionRepository: ActionRepository,
) {
    operator fun invoke(date: LocalDate): Flow<List<Action>> = actionRepository.getActionsForDate(date)
}
