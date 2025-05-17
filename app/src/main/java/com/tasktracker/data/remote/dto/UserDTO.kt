package com.tasktracker.data.remote.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserDTO(
    val id: Int? = null,
    val name: String,
    val email: String
)