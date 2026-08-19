package com.remainder.app.testutil

import com.remainder.app.domain.model.Action
import com.remainder.app.domain.model.Priority
import com.remainder.app.domain.model.ReminderOffset
import com.remainder.app.domain.model.RepeatType
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime

fun sampleAction(
    id: Long = 0,
    title: String = "Sample action",
    notes: String? = null,
    scheduledDate: LocalDate = LocalDate.now().plusDays(1),
    scheduledTime: LocalTime = LocalTime.of(10, 0),
    reminderOffset: ReminderOffset = ReminderOffset.AT_TIME,
    repeatType: RepeatType = RepeatType.NEVER,
    categoryId: Long? = null,
    priority: Priority = Priority.MEDIUM,
    isCompleted: Boolean = false,
    completedAt: Instant? = null,
): Action = Action(
    id = id,
    title = title,
    notes = notes,
    scheduledDate = scheduledDate,
    scheduledTime = scheduledTime,
    reminderOffset = reminderOffset,
    repeatType = repeatType,
    categoryId = categoryId,
    priority = priority,
    isCompleted = isCompleted,
    completedAt = completedAt,
    createdAt = Instant.EPOCH,
    updatedAt = Instant.EPOCH,
)
