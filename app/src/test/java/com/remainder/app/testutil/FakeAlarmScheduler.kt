package com.remainder.app.testutil

import com.remainder.app.domain.model.Action
import com.remainder.app.domain.reminder.AlarmScheduler
import java.time.Instant

class FakeAlarmScheduler : AlarmScheduler {
    val scheduledActionIds = mutableListOf<Long>()
    val snoozedActionIds = mutableListOf<Long>()
    val cancelledActionIds = mutableListOf<Long>()

    override fun schedule(action: Action) {
        scheduledActionIds.add(action.id)
    }

    override fun scheduleSnooze(action: Action, triggerAt: Instant) {
        snoozedActionIds.add(action.id)
    }

    override fun cancel(action: Action) {
        cancelledActionIds.add(action.id)
    }
}
