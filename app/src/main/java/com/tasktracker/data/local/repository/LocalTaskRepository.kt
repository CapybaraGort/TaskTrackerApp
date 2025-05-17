package com.tasktracker.data.local.repository

import com.tasktracker.data.local.dao.TaskDao
import com.tasktracker.data.mapper.toDomain
import com.tasktracker.data.mapper.toTask
import com.tasktracker.domain.entity.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class LocalTaskRepository(private val taskDao: TaskDao) {

    suspend fun addTask(task: Task) {
        taskDao.addTask(task.toDomain())
    }

    suspend fun deleteTask(task: Task) {
        taskDao.deleteTask(task.toDomain())
    }

    suspend fun updateTask(task: Task) {
        taskDao.updateTask(task.toDomain())
    }

    suspend fun deleteAll() {
        taskDao.deleteAll()
    }

    suspend fun getTaskById(id: Int): Task {
        return taskDao.getTaskById(id).first().toTask()
    }
    suspend fun getTasksByCategoryId(id: Int): List<Task> {
        return taskDao.getTasksByCategoryId(id).first().map { it.toTask() }
    }

    fun getAllTasks(): Flow<List<Task>> {
        return taskDao.getAllTasks().map {
            it.map { it.toTask() }
        }
    }
}