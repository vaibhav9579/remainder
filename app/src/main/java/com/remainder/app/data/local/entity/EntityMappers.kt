package com.remainder.app.data.local.entity

import com.remainder.app.domain.model.Action
import com.remainder.app.domain.model.Category

fun ActionEntity.toDomain() = Action(
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
    createdAt = createdAt,
    updatedAt = updatedAt,
)

fun Action.toEntity() = ActionEntity(
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
    createdAt = createdAt,
    updatedAt = updatedAt,
)

fun CategoryEntity.toDomain() = Category(
    id = id,
    name = name,
    icon = icon,
    createdAt = createdAt,
)

fun Category.toEntity() = CategoryEntity(
    id = id,
    name = name,
    icon = icon,
    createdAt = createdAt,
)
