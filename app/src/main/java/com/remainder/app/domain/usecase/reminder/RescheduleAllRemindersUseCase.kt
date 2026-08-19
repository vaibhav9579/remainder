package com.remainder.app.domain.usecase.reminder

import com.remainder.app.domain.reminder.AlarmScheduler
import com.remainder.app.domain.repository.ActionRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * Reschedules every pending action's alarm. Alarms don't survive a reboot,
 * and a previously-computed trigger instant can point at the wrong wall-clock
 * time after the device's time zone or clock changes — both cases call this.
 */
class RescheduleAllRemindersUseCase @Inject constructor(
    private val actionRepository: ActionRepository,
    private val alarmScheduler: AlarmScheduler,
) {
    suspend operator fun invoke() {
        actionRepository.getPendingActions().first().forEach { action -> alarmScheduler.schedule(action) }
    }
}
