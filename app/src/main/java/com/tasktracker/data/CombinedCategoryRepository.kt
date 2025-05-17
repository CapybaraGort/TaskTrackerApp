package com.tasktracker.data

import com.tasktracker.data.local.repository.LocalCategoryRepository
import com.tasktracker.data.remote.auth.AuthManager
import com.tasktracker.data.remote.auth.AuthType
import com.tasktracker.data.remote.repository.RemoteCategoryRepository
import com.tasktracker.domain.entity.Category
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CombinedCategoryRepository @Inject constructor(
    private val local: LocalCategoryRepository,
    private val remote: RemoteCategoryRepository
) {
    fun getCategories(userId: Int? = null): Flow<List<Category>> {
        return if(isAuthenticated()) {
            flow {
                if (userId == null) throw IllegalArgumentException("userId is required for remote categories")
                val remoteCategories = remote.getCategories(userId)
                emit(remoteCategories)
            }
        } else {
            local.getAllCategories()
        }
    }

    suspend fun getCategoryById(id: Int): Category {
        return if(isAuthenticated()) {
            remote.getCategoryById(id)
        } else {
            local.getCategoryById(id).first()
        }
    }

    suspend fun addCategory(category: Category): Category {
        return if(isAuthenticated()) {
            remote.addCategory(category)
        } else {
            local.addCategory(category)
            category
        }
    }

    suspend fun deleteCategory(category: Category): Int? {
        return if(isAuthenticated()) {
            if(category.id == null) throw NullPointerException("category id is null")
            remote.deleteCategoryById(category.id)
        } else {
            local.deleteCategory(category)
            category.id
        }
    }


    private fun isAuthenticated(): Boolean {
        return AuthManager.currentAuthType.value != AuthType.Anonymous
    }
}