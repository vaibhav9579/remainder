package com.remainder.app.domain.usecase.action

import com.remainder.app.domain.model.Action
import com.remainder.app.domain.reminder.AlarmScheduler
import com.remainder.app.domain.repository.ActionRepository
import javax.inject.Inject

class DeleteActionUseCase @Inject constructor(
    private val actionRepository: ActionRepository,
    private val alarmScheduler: AlarmScheduler,
) {
    suspend operator fun invoke(action: Action) {
        alarmScheduler.cancel(action)
        actionRepository.deleteAction(action)
    }
}
