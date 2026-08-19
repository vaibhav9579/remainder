package com.remainder.app.reminder

import com.remainder.app.domain.model.Action
import com.remainder.app.domain.model.Priority
import com.remainder.app.domain.model.ReminderOffset
import com.remainder.app.domain.model.RepeatType
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

class AlarmSchedulerImplTest {

    private fun action(reminderOffset: ReminderOffset) = Action(
        title = "Test action",
        scheduledDate = LocalDate.of(2026, 6, 15),
        scheduledTime = LocalTime.of(14, 0),
        reminderOffset = reminderOffset,
        repeatType = RepeatType.NEVER,
        categoryId = null,
        priority = Priority.MEDIUM,
        createdAt = Instant.EPOCH,
        updatedAt = Instant.EPOCH,
    )

    private fun expected(dateTime: LocalDateTime): Instant =
        dateTime.atZone(ZoneId.systemDefault()).toInstant()

    @Test
    fun atTime_triggersAtScheduledMoment() {
        val result = AlarmSchedulerImpl.triggerTimeFor(action(ReminderOffset.AT_TIME))
        assertEquals(expected(LocalDateTime.of(2026, 6, 15, 14, 0)), result)
    }

    @Test
    fun min15_triggers15MinutesBefore() {
        val result = AlarmSchedulerImpl.triggerTimeFor(action(ReminderOffset.MIN_15))
        assertEquals(expected(LocalDateTime.of(2026, 6, 15, 13, 45)), result)
    }

    @Test
    fun hour1_triggers1HourBefore() {
        val result = AlarmSchedulerImpl.triggerTimeFor(action(ReminderOffset.HOUR_1))
        assertEquals(expected(LocalDateTime.of(2026, 6, 15, 13, 0)), result)
    }

    @Test
    fun day1_triggers1DayBefore() {
        val result = AlarmSchedulerImpl.triggerTimeFor(action(ReminderOffset.DAY_1))
        assertEquals(expected(LocalDateTime.of(2026, 6, 14, 14, 0)), result)
    }
}
