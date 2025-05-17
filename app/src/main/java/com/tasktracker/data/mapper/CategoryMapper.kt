package com.tasktracker.data.mapper

import com.tasktracker.data.local.entity.CategoryEntity
import com.tasktracker.data.remote.dto.CategoryDTO
import com.tasktracker.domain.entity.Category

fun Category.toDomain(): CategoryEntity {
    return CategoryEntity(
        id = this.id ?: 0,
        name = this.name
    )
}

fun CategoryEntity.toDomain(): Category {
    return Category(
        id = this.id,
        name = this.name
    )
}

fun CategoryDTO.toDomain(): Category {
    return Category(
        id = this.id,
        name = this.name,
        userId = this.userId
    )
}

fun Category.toDto(): CategoryDTO {
    return CategoryDTO(
        id = this.id,
        name = this.name,
        userId = this.userId ?: -1
    )
}