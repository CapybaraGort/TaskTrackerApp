package com.tasktracker.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tasktracker.data.CombinedCategoryRepository
import com.tasktracker.data.remote.auth.AuthManager
import com.tasktracker.domain.entity.Category
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val repository: CombinedCategoryRepository
): ViewModel() {

    private val _categoryUiState = MutableStateFlow<CategoryUiState>(CategoryUiState.Loading)
    val categoryUiState get() = _categoryUiState.asStateFlow()

    private val _categories: MutableStateFlow<List<Category>> = MutableStateFlow(emptyList())
    val categories get() = _categories.asStateFlow()

    init {
        viewModelScope.launch {
            AuthManager.currentAuthType.collectLatest {
                loadCategories()
            }
        }
    }

    private suspend fun loadCategories() {
        repository.getCategories(AuthManager.currentUser.id)
            .onStart { _categoryUiState.value = CategoryUiState.Loading }
            .catch { e -> _categoryUiState.value = CategoryUiState.Error(e.message ?: "Unknown error") }
            .collect { list ->
                _categoryUiState.value = CategoryUiState.Success(list)
                _categories.value = list
            }
    }

    suspend fun getCategoryById(id: Int): Category {
        return repository.getCategoryById(id)
    }

    suspend fun addCategory(category: Category) {
        val newCategory = repository.addCategory(category)
        fetchCategories {
            _categories.value = categories.value + newCategory
        }
    }

    fun deleteCategory(category: Category) {
        viewModelScope.launch {
            repository.deleteCategory(category)
            fetchCategories {
                _categories.value = categories.value - category
            }
        }
    }

    private suspend fun fetchCategories(operation: () -> Unit) {
        withContext(Dispatchers.Default) {
            operation()
        }
        withContext(Dispatchers.Main) {
            _categoryUiState.value = CategoryUiState.Success(categories.value.toList())
        }
    }
}

sealed class CategoryUiState {
    object Loading : CategoryUiState()
    data class Success(val categories: List<Category>): CategoryUiState()
    data class Error(val error: String): CategoryUiState()
}