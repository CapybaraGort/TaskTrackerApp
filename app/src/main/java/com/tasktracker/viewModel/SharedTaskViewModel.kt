package com.tasktracker.viewModel

import androidx.lifecycle.ViewModel
import com.tasktracker.domain.entity.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject


@HiltViewModel
class SharedTaskViewModel @Inject constructor() : ViewModel() {
    private val _selectedTask = MutableStateFlow<Task?>(null)
    val selectedTask get() = _selectedTask.asStateFlow()

    fun selectTask(task: Task) {
        _selectedTask.value = task
    }

    fun clearTask() {
        _selectedTask.value = null
    }
}