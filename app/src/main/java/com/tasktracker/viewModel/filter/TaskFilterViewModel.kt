package com.tasktracker.viewModel.filter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tasktracker.domain.entity.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class TaskFilterViewModel @Inject constructor()  : ViewModel() {

    private val _currentFilter = MutableStateFlow<(Task) -> Boolean> { true }

    var selectedCategoryId = MutableStateFlow<Int?>(null)
        private set

    var selectedBeginDate = MutableStateFlow<LocalDate?>(null)
        private set

    var selectedDeadline = MutableStateFlow<LocalDate?>(null)
        private set

    var isTasksCompleted = MutableStateFlow<Boolean?>(null)
        private set

    fun filteredTasks(sourceTasks: StateFlow<List<Task>>): StateFlow<List<Task>> {
        return combine(sourceTasks, _currentFilter) { tasks, filter ->
            tasks.filter(filter)
        }.stateIn(viewModelScope, SharingStarted.Companion.Lazily, emptyList())
    }

    fun updateFilter() {
        _currentFilter.value = { task ->
            val categoryMatches = selectedCategoryId.value == null || task.categoryId == selectedCategoryId.value
            val beginDateMatches = selectedBeginDate.value == null || task.begin.dayOfMonth == selectedBeginDate.value?.dayOfMonth
            val deadlineMatches = selectedDeadline.value == null || task.deadline.dayOfMonth == selectedDeadline.value?.dayOfMonth
            categoryMatches && beginDateMatches && deadlineMatches
        }
    }

    fun clearFilter() {
        _currentFilter.value = { true }
        selectedCategoryId.value = null
        selectedBeginDate.value = null
        selectedDeadline.value = null
        isTasksCompleted.value = null
    }

    fun setCategoryId(id: Int) {
        selectedCategoryId.value = id
    }

    fun setBeginDateTime(date: LocalDate?) {
        selectedBeginDate.value = date
    }

    fun setDeadline(date: LocalDate?) {
        selectedDeadline.value = date
    }

    fun resetCategoryId() {
        selectedCategoryId.value = null
    }
}