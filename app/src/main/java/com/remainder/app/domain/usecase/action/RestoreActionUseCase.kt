package com.remainder.app.domain.usecase.action

import com.remainder.app.domain.model.Action
import com.remainder.app.domain.reminder.AlarmScheduler
import com.remainder.app.domain.repository.ActionRepository
import java.time.Instant
import javax.inject.Inject

class RestoreActionUseCase @Inject constructor(
    private val actionRepository: ActionRepository,
    private val alarmScheduler: AlarmScheduler,
) {
    suspend operator fun invoke(action: Action) {
        val restored = action.copy(isCompleted = false, completedAt = null, updatedAt = Instant.now())
        actionRepository.updateAction(restored)
        alarmScheduler.schedule(restored)
    }
}
