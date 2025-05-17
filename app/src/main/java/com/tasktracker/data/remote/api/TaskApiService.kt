package com.tasktracker.data.remote.api

import com.squareup.moshi.JsonClass
import com.tasktracker.data.remote.dto.TaskDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface TaskApiService {
    @GET("users/{user_id}/tasks")
    suspend fun getTasks(@Path("user_id") userId: Int): Response<ApiResponse<TaskList>>

    @GET("users/tasks/{task_id}")
    suspend fun getTaskById(@Path("task_id") taskId: Int): Response<ApiResponse<TaskDTO>>

    @POST("tasks")
    suspend fun addTask(@Body task: TaskDTO): Response<ApiResponse<TaskDTO>>

    @PUT("/users/tasks/{id}")
    suspend fun updateTask(@Path("id") id: Int, @Body task: TaskDTO): Response<ApiResponse<TaskDTO>>

    @DELETE("/users/tasks/{id}")
    suspend fun deleteTask(@Path("id") id: Int): Response<ApiResponse<IntResponse>>
}

@JsonClass(generateAdapter = true)
data class TaskList(val tasks: List<TaskDTO>)