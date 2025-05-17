package com.tasktracker.data.remote.repository

import com.tasktracker.data.mapper.toDomain
import com.tasktracker.data.mapper.toDto
import com.tasktracker.data.remote.api.CategoryApiService
import com.tasktracker.data.remote.api.CategoryList
import com.tasktracker.data.remote.api.IntResponse
import com.tasktracker.data.remote.api.ResponseHandler
import com.tasktracker.data.remote.dto.CategoryDTO
import com.tasktracker.domain.entity.Category
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RemoteCategoryRepository @Inject constructor(
    private val categoryApiService: CategoryApiService,
    private val handler: ResponseHandler
) {
    suspend fun getCategoryById(id: Int): Category = withContext(Dispatchers.IO) {
        val result = handler.handle<CategoryDTO>(categoryApiService.getCategoryById(id))

        result.getOrThrow().toDomain()
    }

    suspend fun getCategories(userId: Int): List<Category> = withContext(Dispatchers.IO) {
        val result = handler.handle<CategoryList>(categoryApiService.getCategories(userId))

        result.getOrThrow().categories.map { it.toDomain() }
    }

    suspend fun addCategory(category: Category): Category = withContext(Dispatchers.IO) {
        val result = handler.handle<CategoryDTO>(categoryApiService.addCategory(category.toDto()))

        result.getOrThrow().toDomain()
    }

    suspend fun deleteCategoryById(id: Int): Int? = withContext(Dispatchers.IO) {
        val result = handler.handle(categoryApiService.deleteCategory(id))

        result.getOrThrow().value
    }
}