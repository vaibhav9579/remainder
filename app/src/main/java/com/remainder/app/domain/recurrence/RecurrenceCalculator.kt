package com.remainder.app.domain.recurrence

import com.remainder.app.domain.model.RepeatType
import java.time.LocalDate

/**
 * Advances a scheduled date by exactly one recurrence interval. Always steps
 * forward from the action's own scheduled date, not from "now" — a task left
 * uncompleted for several days moves to the day after its last due date, not
 * to today, keeping the behavior predictable. Custom recurrence is not
 * supported; only the four fixed intervals from the spec are.
 */
object RecurrenceCalculator {
    fun nextOccurrence(date: LocalDate, repeatType: RepeatType): LocalDate = when (repeatType) {
        RepeatType.NEVER -> date
        RepeatType.DAILY -> date.plusDays(1)
        RepeatType.WEEKLY -> date.plusWeeks(1)
        RepeatType.MONTHLY -> date.plusMonths(1)
        RepeatType.YEARLY -> date.plusYears(1)
    }
}
