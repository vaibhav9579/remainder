package com.remainder.app.domain.usecase.action

import com.remainder.app.domain.model.Action
import com.remainder.app.domain.reminder.AlarmScheduler
import com.remainder.app.domain.repository.ActionRepository
import java.time.Instant
import javax.inject.Inject

class AddActionUseCase @Inject constructor(
    private val actionRepository: ActionRepository,
    private val alarmScheduler: AlarmScheduler,
) {
    suspend operator fun invoke(action: Action): Long {
        val now = Instant.now()
        val toSave = action.copy(createdAt = now, updatedAt = now)
        val id = actionRepository.addAction(toSave)
        alarmScheduler.schedule(toSave.copy(id = id))
        return id
    }
}
