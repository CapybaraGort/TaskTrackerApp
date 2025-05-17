package com.tasktracker.data.mapper

import com.tasktracker.data.remote.dto.UserDTO
import com.tasktracker.domain.entity.User

fun UserDTO.toDomain(): User {
    return User(
        id = id,
        name = name,
        email = email
    )
}

fun User.toDto(): UserDTO {
    return UserDTO(
        id = id,
        name = name,
        email = email
    )
}