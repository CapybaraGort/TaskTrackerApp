package com.tasktracker.viewModel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class CategorySelectViewModel: ViewModel() {
    private val _selectedId = MutableStateFlow<Int?>(null)
    val selectedId get() = _selectedId.asStateFlow()

    private val _categoryName = MutableStateFlow<String>("")
    val categoryName get() = _categoryName.asStateFlow()

    fun selectCategory(id: Int?) {
        _selectedId.value = id
    }

    fun setCategoryName(name: String) {
        _categoryName.value = name
    }
}