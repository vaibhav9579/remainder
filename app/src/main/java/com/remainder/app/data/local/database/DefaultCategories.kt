package com.remainder.app.data.local.database

import com.remainder.app.data.local.entity.CategoryEntity
import java.time.Instant

object DefaultCategories {
    fun seed(): List<CategoryEntity> {
        val now = Instant.now()
        return listOf(
            CategoryEntity(name = "Work", icon = "work", createdAt = now),
            CategoryEntity(name = "Personal", icon = "personal", createdAt = now),
            CategoryEntity(name = "Finance", icon = "finance", createdAt = now),
            CategoryEntity(name = "Shopping", icon = "shopping", createdAt = now),
            CategoryEntity(name = "Meeting", icon = "meeting", createdAt = now),
            CategoryEntity(name = "Other", icon = "other", createdAt = now),
        )
    }
}
