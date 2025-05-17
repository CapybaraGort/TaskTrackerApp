package com.tasktracker.data.remote.repository

import com.tasktracker.data.mapper.toDomain
import com.tasktracker.data.mapper.toDto
import com.tasktracker.data.remote.api.ResponseHandler
import com.tasktracker.data.remote.api.TaskApiService
import com.tasktracker.domain.entity.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RemoteTaskRepository @Inject constructor(
    private val apiService: TaskApiService,
    private val handler: ResponseHandler
) {

    suspend fun getTasks(userId: Int): List<Task> = withContext(Dispatchers.IO) {
        val result = handler.handle(apiService.getTasks(userId))

        result.getOrThrow().tasks.map { it.toDomain() }
    }

    suspend fun getTaskById(id: Int): Task = withContext(Dispatchers.IO) {
        val result = handler.handle(apiService.getTaskById(id))

        result.getOrThrow().toDomain()
    }

    suspend fun addTask(task: Task): Task = withContext(Dispatchers.IO) {
        val result = handler.handle(apiService.addTask(task.toDto()))

        result.getOrThrow().toDomain()
    }

    suspend fun updateTask(task: Task): Task = withContext(Dispatchers.IO) {
        val result = handler.handle(apiService.updateTask(task.id ?: -1, task.toDto()))

        result.getOrThrow().toDomain()
    }

    suspend fun deleteTask(id: Int): Int? = withContext(Dispatchers.IO) {
        val result = handler.handle(apiService.deleteTask(id))

        result.getOrThrow().value
    }

}
