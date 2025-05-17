package com.tasktracker.data.local.repository

import com.tasktracker.data.local.dao.CategoryDao
import com.tasktracker.data.mapper.toDomain
import com.tasktracker.domain.entity.Category
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LocalCategoryRepository @Inject constructor(
    private val categoryDao: CategoryDao
) {
    fun getAllCategories(): Flow<List<Category>> {
        return categoryDao.getAllCategories().map {
            it.map { it.toDomain() }
        }
    }

    fun getCategoryById(id: Int): Flow<Category> {
        return categoryDao.getCategoryById(id).map {
            it.toDomain()
        }
    }

    suspend fun addCategory(category: Category) {
        categoryDao.insertCategory(category.toDomain())
    }

    suspend fun deleteCategory(category: Category) {
        categoryDao.deleteCategory(category.toDomain())
    }
}