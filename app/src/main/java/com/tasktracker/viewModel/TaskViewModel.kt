package com.tasktracker.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.tasktracker.data.CombinedTaskRepository
import com.tasktracker.data.remote.auth.AuthManager
import com.tasktracker.domain.entity.Task
import com.tasktracker.work.cancelTaskNotification
import com.tasktracker.work.scheduleTaskNotification
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val combinedTaskRepository: CombinedTaskRepository,
    private val application: Application
) : AndroidViewModel(application) {

    var taskUiState = MutableStateFlow<TaskUiState>(TaskUiState.Loading)
        private set

    private val _tasks: MutableStateFlow<List<Task>> = MutableStateFlow(emptyList())
    val tasks get() = _tasks.asStateFlow()

    init {
        viewModelScope.launch {
            AuthManager.currentAuthType.collectLatest {
                loadTasks()
            }
        }
    }

    private suspend fun loadTasks() {
        combinedTaskRepository.getTasks(AuthManager.currentUser.value.id)
            .onStart { taskUiState.value = TaskUiState.Loading }
            .catch { e -> taskUiState.value = TaskUiState.Error(e.message ?: "Unknown error") }
            .collect { taskList ->
                taskUiState.value = TaskUiState.Success(taskList)
                _tasks.value = taskList
            }
    }

    fun addTask(task: Task) {
        viewModelScope.launch {
            taskUiState.value = TaskUiState.Loading
            val newTask = combinedTaskRepository.addTask(task)
            fetchTasks {
                _tasks.value = _tasks.value + newTask
            }
            scheduleTaskNotification(application, newTask, newTask.begin)
            scheduleTaskNotification(application, newTask, newTask.deadline)
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            combinedTaskRepository.deleteTask(task)
            fetchTasks { _tasks.value = _tasks.value - task }
            cancelTaskNotification(application, task)
        }
    }

    suspend fun getTaskById(id: Int): Task {
        return combinedTaskRepository.getTaskById(id)
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            val updTask = combinedTaskRepository.updateTask(task)

            fetchTasks {
                _tasks.value = _tasks.value.map {
                    if (it.id == updTask.id) updTask else it
                }
            }
            cancelTaskNotification(application, task)
            scheduleTaskNotification(application, updTask, updTask.begin)
            scheduleTaskNotification(application, updTask, updTask.deadline)
        }
    }

    private suspend fun fetchTasks(operation: suspend () -> Unit) {
        operation()
        taskUiState.value = TaskUiState.Success(_tasks.value.toList())
    }
}

sealed class TaskUiState {
    object Loading : TaskUiState()
    data class Success(val result: List<Task>) : TaskUiState()
    data class Error(val error: String) : TaskUiState()
}