package com.tasktracker.data.remote.repository

import com.tasktracker.data.mapper.toDto
import com.tasktracker.data.mapper.toDomain
import com.tasktracker.data.remote.api.ApiResponse
import com.tasktracker.data.remote.api.ResponseHandler
import com.tasktracker.data.remote.api.UserApiService
import com.tasktracker.domain.entity.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RemoteUserRepository @Inject constructor(
    private val userApiService: UserApiService,
    private val handler: ResponseHandler
) {
    suspend fun getUserByEmail(email: String): User? = withContext(Dispatchers.IO) {
        val result = handler.handle(userApiService.getUserByEmail(email))

        result.getOrThrow()?.toDomain() ?: throw NullPointerException("Null user")
    }

    suspend fun getUserById(id: Int): User = withContext(Dispatchers.IO) {
        val result = handler.handle(userApiService.getUserById(id))

        result.getOrThrow().toDomain()
    }

    suspend fun addUser(user: User): User = withContext(Dispatchers.IO) {
        val result = handler.handle(userApiService.addUser(user.toDto()))

        result.getOrThrow().toDomain()
    }

    suspend fun updateUser(user: User): User = withContext(Dispatchers.IO) {
        val result = handler.handle(userApiService.updateUser(user.id ?: 0, user.toDto()))

        result.getOrThrow().toDomain()
    }

    suspend fun deleteUserById(id: Int): Int = withContext(Dispatchers.IO) {
        val result = handler.handle(userApiService.deleteUser(id))

        result.getOrThrow().value
    }
}