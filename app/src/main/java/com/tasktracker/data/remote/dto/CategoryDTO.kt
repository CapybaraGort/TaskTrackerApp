package com.tasktracker.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CategoryDTO(
    val id: Int? = null,
    @Json(name = "user_id") val userId: Int,
    val name: String
)
