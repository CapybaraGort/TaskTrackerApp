package com.tasktracker.domain.entity

import java.time.LocalDateTime
import java.util.UUID

data class Task(
    val id: Int? = null,
    val title: String,
    val description: String,
    val begin: LocalDateTime,
    val deadline: LocalDateTime,
    val isCompleted: Boolean,
    val categoryId: Int,
    val userId: Int? = null
)