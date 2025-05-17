package com.tasktracker.data

import com.tasktracker.data.local.repository.LocalTaskRepository
import com.tasktracker.data.remote.auth.AuthManager
import com.tasktracker.data.remote.auth.AuthType
import com.tasktracker.data.remote.repository.RemoteTaskRepository
import com.tasktracker.domain.entity.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CombinedTaskRepository @Inject constructor(
    private val local: LocalTaskRepository,
    private val remote: RemoteTaskRepository
) {
    fun getTasks(userId: Int? = null): Flow<List<Task>> {
        return if (isNotAuthenticated()) {
            local.getAllTasks()
        } else {
            flow {
                if (userId == null) throw IllegalArgumentException("userId is required for remote tasks")
                val remoteTasks = remote.getTasks(userId)
                emit(remoteTasks)
            }
        }
    }

    suspend fun getTaskById(id: Int): Task {
        return if (isNotAuthenticated()) {
            local.getTaskById(id)
        } else {
            remote.getTaskById(id)
        }
    }

    suspend fun addTask(task: Task): Task {
        return if (isNotAuthenticated()) {
            local.addTask(task)
            task
        } else {
            remote.addTask(task)
        }
    }

    suspend fun updateTask(task: Task): Task {
        return if (isNotAuthenticated()) {
            local.updateTask(task)
            task
        } else {
            remote.updateTask(task)
        }
    }

    suspend fun deleteTask(task: Task): Int? {
        return if (isNotAuthenticated()) {
            local.deleteTask(task)
            task.id
        } else {
            if (task.id == null) throw NullPointerException("Task ID required for remote delete")
            remote.deleteTask(task.id)
        }
    }

    private fun isNotAuthenticated(): Boolean {
        return AuthManager.currentAuthType.value == AuthType.Anonymous
    }
}
