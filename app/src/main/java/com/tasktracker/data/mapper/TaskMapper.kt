package com.tasktracker.data.mapper

import com.tasktracker.data.local.entity.TaskEntity
import com.tasktracker.data.remote.dto.TaskDTO
import com.tasktracker.domain.entity.Task

fun TaskEntity.toTask(): Task {
    return Task(
        id = this.id,
        title = this.title,
        description = this.description,
        deadline = this.deadline,
        categoryId = this.categoryId,
        isCompleted = this.isCompleted,
        begin = begin
    )
}

fun Task.toDomain(): TaskEntity {
    return TaskEntity(
        id = id ?: 0,
        title = this.title,
        description = this.description,
        deadline = this.deadline,
        begin = begin,
        categoryId = this.categoryId,
        isCompleted = this.isCompleted
    )
}


fun TaskDTO.toDomain(): Task {
    return Task(
        id = id,
        title = title,
        description = description,
        deadline = deadline,
        userId = userId,
        categoryId = categoryId,
        isCompleted = isCompleted,
        begin = begin
    )
}

fun Task.toDto(): TaskDTO {
    return TaskDTO(
        id = id,
        title = title,
        description = description,
        deadline = deadline,
        categoryId = categoryId,
        isCompleted = isCompleted,
        begin = begin,
        userId = userId ?: 0
    )
}