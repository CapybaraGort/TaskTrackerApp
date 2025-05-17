package com.tasktracker.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.time.LocalDateTime

@JsonClass(generateAdapter = true)
data class TaskDTO(
    @Json(name = "id") val id: Int? = null,
    @Json(name = "title") val title: String,
    @Json(name = "description") val description: String,
    @Json(name = "user_id") val userId: Int,
    @Json(name = "deadline") val deadline: LocalDateTime = LocalDateTime.now(),
    @Json(name = "begin") val begin: LocalDateTime = LocalDateTime.now(),
    @Json(name = "is_completed") val isCompleted: Boolean = false,
    @Json(name = "category_id") val categoryId: Int
)