package com.remainder.app.domain.model

import java.time.Instant

data class Category(
    val id: Long = 0,
    val name: String,
    val icon: String,
    val createdAt: Instant,
)
