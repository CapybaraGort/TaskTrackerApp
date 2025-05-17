package com.tasktracker.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tasktracker.domain.entity.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SelectTaskViewModel: ViewModel() {
    private val _selectedTasks = MutableStateFlow<Set<Task>>(emptySet())
    val selectedTasks get() = _selectedTasks.asStateFlow()

    fun toggleTaskSelection(task: Task) {
        viewModelScope.launch {
            _selectedTasks.update { selected ->
                if(selected.contains(task)) {
                    selected - task
                } else {
                    selected + task
                }
            }
        }
    }

    fun clearSelection() {
        _selectedTasks.value = emptySet()
    }
}