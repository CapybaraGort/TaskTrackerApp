package com.tasktracker.data.remote.api

import com.squareup.moshi.JsonClass
import com.tasktracker.data.remote.dto.UserDTO
import kotlinx.serialization.Serializable
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface UserApiService {

    @GET("users/{email}")
    suspend fun getUserByEmail(@Path("email") email: String): Response<ApiResponse<UserDTO?>>

    @POST("users")
    suspend fun addUser(@Body user: UserDTO): Response<ApiResponse<UserDTO>>

    @GET("users/{id}")
    suspend fun getUserById(@Path("id") id: Int): Response<ApiResponse<UserDTO>>

    @PUT("users/{id}")
    suspend fun updateUser(@Path("id") id: Int, @Body user: UserDTO): Response<ApiResponse<UserDTO>>

    @DELETE("users/{id}")
    suspend fun deleteUser(@Path("id") id: Int): Response<ApiResponse<IntResponse>>
}

@JsonClass(generateAdapter = true)
@Serializable
data class ApiResponse<T>(
    val data: T? = null,
    val message: String? = null,
    val isError: Boolean = false
)

@JsonClass(generateAdapter = true)
data class IntResponse(
    val value: Int
)