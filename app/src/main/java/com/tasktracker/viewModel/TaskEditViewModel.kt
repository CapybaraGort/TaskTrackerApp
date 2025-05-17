package com.tasktracker.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tasktracker.domain.entity.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class TaskEditViewModel @Inject constructor() : ViewModel() {
    private val _task : MutableStateFlow<Task?> = MutableStateFlow(null)
    val task: StateFlow<Task?> get() = _task

    private val _title = MutableStateFlow<String>("")
    val title: StateFlow<String> get() = _title

    private val _description = MutableStateFlow<String>("")
    val description: StateFlow<String> get() = _description

    private val _deadline = MutableStateFlow<LocalDateTime>(LocalDateTime.now())
    val deadline: StateFlow<LocalDateTime> get() = _deadline

    private val _begin = MutableStateFlow<LocalDateTime>(LocalDateTime.now())
    val begin: StateFlow<LocalDateTime> get() = _begin

    private val _categoryId = MutableStateFlow<Int?>(null)
    val categoryId: StateFlow<Int?> get() = _categoryId.asStateFlow()

    fun setTitle(title: String) {
        _title.value = title
    }

    fun setDescription(description: String) {
        _description.value = description
    }

    fun setTask(tsk: Task) {
        _task.value = tsk
        setDeadline(tsk.deadline)
        setDescription(tsk.description)
        setTitle(tsk.title)
        setCategoryId(tsk.categoryId)
        setBegin(tsk.begin)
    }

    fun setBegin(time: LocalDateTime) {
        _begin.value = time
    }

    fun setDeadline(time: LocalDateTime) {
        _deadline.value = time
    }

    fun setCategoryId(id: Int?) {
        _categoryId.value = id
    }

    fun initCategoryId(value: Int?): StateFlow<Int?> {
        return flow {
            emit(value)
        }.stateIn(scope = viewModelScope, started = SharingStarted.Eagerly, initialValue = value)
    }
}