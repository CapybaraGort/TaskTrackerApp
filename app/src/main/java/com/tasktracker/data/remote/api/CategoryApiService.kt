package com.tasktracker.data.remote.api

import com.squareup.moshi.JsonClass
import com.tasktracker.data.remote.dto.CategoryDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface CategoryApiService {
    @GET("/users/categories/{category_id}")
    suspend fun getCategoryById(@Path("category_id") categoryId: Int): Response<ApiResponse<CategoryDTO>>

    @GET("/users/{user_id}/categories")
    suspend fun getCategories(@Path("user_id") userId: Int): Response<ApiResponse<CategoryList>>

    @POST("/users/categories")
    suspend fun addCategory(@Body category: CategoryDTO): Response<ApiResponse<CategoryDTO>>

    @DELETE("/users/categories/{category_id}")
    suspend fun deleteCategory(@Path("category_id") categoryId: Int): Response<ApiResponse<IntResponse>>
}

@JsonClass(generateAdapter = true)
data class CategoryList(val categories: List<CategoryDTO>)