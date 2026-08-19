package com.remainder.app.domain.usecase.action

import com.remainder.app.domain.model.Action
import com.remainder.app.domain.model.RepeatType
import com.remainder.app.domain.recurrence.RecurrenceCalculator
import com.remainder.app.domain.reminder.AlarmScheduler
import com.remainder.app.domain.repository.ActionRepository
import java.time.Instant
import javax.inject.Inject

class CompleteActionUseCase @Inject constructor(
    private val actionRepository: ActionRepository,
    private val alarmScheduler: AlarmScheduler,
) {
    suspend operator fun invoke(action: Action) {
        if (action.repeatType == RepeatType.NEVER) {
            val now = Instant.now()
            val completed = action.copy(isCompleted = true, completedAt = now, updatedAt = now)
            actionRepository.updateAction(completed)
            alarmScheduler.cancel(completed)
        } else {
            // Recurring actions never land in Completed — finishing one just
            // rolls it forward to its next occurrence and stays pending.
            val next = action.copy(
                scheduledDate = RecurrenceCalculator.nextOccurrence(action.scheduledDate, action.repeatType),
                isCompleted = false,
                completedAt = null,
                updatedAt = Instant.now(),
            )
            actionRepository.updateAction(next)
            alarmScheduler.schedule(next)
        }
    }
}
