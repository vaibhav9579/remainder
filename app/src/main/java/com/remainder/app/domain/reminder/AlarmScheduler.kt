package com.remainder.app.domain.reminder

import com.remainder.app.domain.model.Action
import java.time.Instant

/**
 * Abstraction the domain layer schedules reminders through. The Android-specific
 * implementation (AlarmManager) lives in the top-level `reminder` package.
 */
interface AlarmScheduler {
    fun schedule(action: Action)
    fun scheduleSnooze(action: Action, triggerAt: Instant)
    fun cancel(action: Action)
}
